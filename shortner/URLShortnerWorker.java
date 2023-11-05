//package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class URLShortnerWorker implements Runnable {
	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "files/index.html";
	static final String FILE_NOT_FOUND = "files/404.html";
	static final String METHOD_NOT_SUPPORTED = "files/not_supported.html";
	static final String REDIRECT_RECORDED = "files/redirect_recorded.html";
	static final String REDIRECT = "files/redirect.html";
	static final String BAD_REQUEST = "files/400.html";

	private final Pattern PUT_CLIENT = Pattern.compile("^PUT\\s+/\\?short=(\\S+)&long=(\\S+)\\s+(\\S+)$");
	private final Pattern GET_CLIENT = Pattern.compile("^GET\\s+/([\\S+&&[^&=]]+)\\s+(\\S+)$");
	private final Pattern GET_INITIAL = Pattern.compile("^GET\\s+/\\s+(\\S+)$");

	// verbose mode
	private boolean VERBOSE;

	private int threadId;
	private ThreadWork work;
	private boolean isCacheEnabled;
	private boolean isWriteBufferEnabled;

	// specify 0 for no cache/buffer
	public URLShortnerWorker(int threadId, ThreadWork work, boolean verbose) {
		this.VERBOSE = verbose;

		this.threadId = threadId;
		this.work = work;
		this.isCacheEnabled = true;
		this.isWriteBufferEnabled = true;
	}

	public void run() {
		// wait for socket to become available - check socket queue
		while (true) {
			Socket newConn = null;
			try {
				newConn = work.getQueue().dequeue();
				handle(newConn);
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				try {
                    if (newConn != null) {
						newConn.close();
					}
                } catch (IOException e) {
                    System.err.println(e);
                }
			}
		}
	}

	public void handle(Socket connect) throws IOException {
		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));

			String input = in.readLine();

			if (VERBOSE) {
				System.out.println(new Date() + ": Thread " + this.threadId + ": " + input);
			}

			// persist URL
			Matcher mput = PUT_CLIENT.matcher(input);
			if (mput.matches()) {
				String shortResource = mput.group(1);
				String longResource = mput.group(2);

				handleClientPut(connect, in, shortResource, longResource);
				return;
			}

			// retrieve URL
			Matcher mget = GET_CLIENT.matcher(input);
			if (mget.matches()) {
				String shortResource = mget.group(1);

				handleClientGet(connect, in, shortResource);
				return;
			}

			// load form when user initially visits
			Matcher mgetLoad = GET_INITIAL.matcher(input);
			if (mgetLoad.matches()) {
				handleInitialGet(connect, in);
				return;
			}

			handleDefault(connect, in);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}	
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}

			if (VERBOSE) {
				System.out.println(new Date() + ": Thread " + this.threadId + ": Connection closed");
			}
		}
	}

	// load a file into memory
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];

		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
		}

		return fileData;
	}

	private void handleInitialGet(Socket connect, BufferedReader in) {
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		try {
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());

			File file = new File(WEB_ROOT, DEFAULT_FILE);
			int fileLength = (int) file.length();
			String contentMimeType = "text/html";
			// read content to return to client
			byte[] fileData = readFileData(file, fileLength);

			out.println("HTTP/1.1 200 OK");
			out.println("Server: Java HTTP Server/Shortner : 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + contentMimeType);
			out.println("Content-length: " + fileLength);
			out.println();
			out.flush();

			dataOut.write(fileData, 0, fileLength);
			dataOut.flush();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (dataOut != null) {
					dataOut.close();
				}
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
		}
	}

	private void handleClientGet(Socket connect, BufferedReader in, String shortResource) throws IOException {
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		try {
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());

			String longResource = null;
			if (isCacheEnabled) {
				longResource = this.work.getCache().get(shortResource); // check in cache first
				if (longResource == null) {
					longResource = this.work.getUrlDao().get(shortResource);
					this.work.getCache().set(shortResource, longResource);
				}
			} else {
				longResource = this.work.getUrlDao().get(shortResource);
			}

			if (longResource != null) { // case 1: URL exists - display success page
				File file = new File(WEB_ROOT, REDIRECT);
				int fileLength = (int) file.length();
				String contentMimeType = "text/html";

				// read content to return to client
				byte[] fileData = readFileData(file, fileLength);

				out.println("HTTP/1.1 307 Temporary Redirect");
				out.println("Location: " + longResource);
				out.println("Server: Java HTTP Server/Shortner : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println();
				out.flush();

				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
			} else { // case 2: URL doesn't exist
				File file = new File(WEB_ROOT, FILE_NOT_FOUND);
				int fileLength = (int) file.length();
				String content = "text/html";
				byte[] fileData = readFileData(file, fileLength);

				out.println("HTTP/1.1 404 Not Found");
				out.println("Server: Java HTTP Server/Shortner : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + content);
				out.println("Content-length: " + fileLength);
				out.println();
				out.flush();

				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (dataOut != null) {
					dataOut.close();
				}
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
		}
    }

	private void handleClientPut(Socket connect, BufferedReader in, String shortResource, String longResource) {
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		try {
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());

			if (isWriteBufferEnabled) {
				this.work.getWriteBuffer().put(shortResource, longResource); // buffer automatically flushes
			} else {
				this.work.getUrlDao().set(shortResource, longResource);
			}

			if (isCacheEnabled) {
				this.work.getCache().set(shortResource, longResource);
			}

			File file = new File(WEB_ROOT, REDIRECT_RECORDED);
			int fileLength = (int) file.length();
			String contentMimeType = "text/html";
			// read content to return to client
			byte[] fileData = readFileData(file, fileLength);

			out.println("HTTP/1.1 400 Bad Request");
			out.println("Server: Java HTTP Server/Shortner : 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + contentMimeType);
			out.println("Content-length: " + fileLength);
			out.println();
			out.flush();

			dataOut.write(fileData, 0, fileLength);
			dataOut.flush();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (dataOut != null) {
					dataOut.close();
				}
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
		}
	}

	private void handleDefault(Socket connect, BufferedReader in) throws IOException {
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		try {
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());


			File file = new File(WEB_ROOT, BAD_REQUEST);
			int fileLength = (int) file.length();
			String contentMimeType = "text/html";
			// read content to return to client
			byte[] fileData = readFileData(file, fileLength);

			out.println("HTTP/1.1 200 OK");
			out.println("Server: Java HTTP Server/Shortner : 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + contentMimeType);
			out.println("Content-length: " + fileLength);
			out.println();
			out.flush();

			dataOut.write(fileData, 0, fileLength);
			dataOut.flush();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (dataOut != null) {
					dataOut.close();
				}
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
		}
    }
}