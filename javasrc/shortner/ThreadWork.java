//package server;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.net.Socket;
import common.CassandraDao;
import common.RedisDao;

/**
 * Collection of data necessary for concurrent thread work.
 * 
 * Includes
 * - a synchronized queue of sockets that threads can pull work from to handle
 * client connections
 * - a synchronized cache for storing recent requests
 * - a synchronized write buffer for containing write data that will be
 * persisted later on
 */
public class ThreadWork {

	class SocketQueue {
		private List<Socket> queue;

		SocketQueue() {
			this.queue = new LinkedList<Socket>();
		}

		public synchronized void enqueue(Socket client) throws InterruptedException { // no limit?
			this.queue.add(client);
			notifyAll();
		}

		public synchronized Socket dequeue() throws InterruptedException {
			while (!isAvailable()) {
				wait();
			}
			Socket client = this.queue.remove(0);
			notifyAll();
			return client;
		}

		private boolean isAvailable() {
			return this.queue.size() > 0;
		}
	}

	class WriteBuffer {
		private HashMap<String, String> buffer;

		WriteBuffer() {
			this.buffer = new HashMap<String, String>();
		}

		public synchronized int getSize() {
			return this.buffer.size();
		}

		public int getMaxSize() {
			return 100000;
		}

		// dump and re-initialize buffer
		public synchronized HashMap<String, String> flush() throws InterruptedException { // called by write thread
			/*
			 * while (!isFull()) {
			 * wait();
			 * }
			 */
			HashMap<String, String> currBuffer = this.buffer;
			this.buffer = new HashMap<String, String>();

			return currBuffer;
		}

		public synchronized void put(String key, String value) throws InterruptedException {
			this.buffer.put(key, value);

			/*
			 * if (isFull()) {
			 * notifyAll(); // wake up write thread if necessary
			 * }
			 */
		}
	}

	private SocketQueue queue;
	private Cache cache;
	private WriteBuffer buffer;
	private CassandraDao dao;

	public ThreadWork(RedisDao redisDao, CassandraDao cassDao) {
		this.queue = new SocketQueue();
		this.cache = new Cache(redisDao);
		this.buffer = new WriteBuffer(); // TODO?
		this.dao = cassDao; // TODO
	}

	public SocketQueue getQueue() {
		return this.queue;
	}

	public Cache getCache() {
		return this.cache;
	}

	public WriteBuffer getWriteBuffer() {
		return this.buffer;
	}

	public CassandraDao getUrlDao() {
		return this.dao;
	}
}