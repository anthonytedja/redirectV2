//package shortner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import common.CassandraDao;
import common.RedisDao;

public class URLShortnerOptimized {

	// configurable parameters
	static boolean IS_VERBOSE; // toggle log statements
	static int PORT;
	static int NUM_THREADS; // 4
	static String REDIS_READ_MASTER_HOSTNAME;
	static String REDIS_READ_SLAVE_HOSTNAME;
	static String REDIS_WRITE_HOSTNAME;
	static int REDIS_READ_PORT;
	static int REDIS_WRITE_PORT;
	static String CASSANDRA_HOSTNAME;
	static int CASSANDRA_PORT;
	static String CASSANDRA_KEYSPACE;
	static String CASSANDRA_DATACENTER;

	public static void main(String[] args) {
		try {
			IS_VERBOSE = Boolean.parseBoolean(args[0]);
			PORT = Integer.parseInt(args[1]);
			NUM_THREADS = Integer.parseInt(args[2]);
			REDIS_READ_MASTER_HOSTNAME = args[3];
			REDIS_READ_SLAVE_HOSTNAME = args[4];
			REDIS_WRITE_HOSTNAME = args[5];
			REDIS_READ_PORT = Integer.parseInt(args[6]);
			REDIS_WRITE_PORT = Integer.parseInt(args[7]);
			CASSANDRA_HOSTNAME = args[8];
			CASSANDRA_PORT = Integer.parseInt(args[9]);
			CASSANDRA_KEYSPACE = args[10];
			CASSANDRA_DATACENTER = args[11];

			RedisDao cacheDao = new RedisDao(REDIS_READ_MASTER_HOSTNAME, REDIS_READ_SLAVE_HOSTNAME, REDIS_READ_PORT);
			RedisDao writeBufferDao = new RedisDao(REDIS_WRITE_HOSTNAME, REDIS_WRITE_PORT);
			CassandraDao databaseDao = new CassandraDao(CASSANDRA_HOSTNAME, CASSANDRA_PORT, CASSANDRA_KEYSPACE, CASSANDRA_DATACENTER);

			ThreadWork work = new ThreadWork(cacheDao, writeBufferDao, databaseDao);

			// start up worker threads to handle general URL shortening functionality
			Thread[] worker = new Thread[NUM_THREADS];
			for (int i = 0; i < NUM_THREADS; i++) {
				worker[i] = new Thread(new URLShortnerWorker(i, work, IS_VERBOSE));
				worker[i].start();
			}

			try (ServerSocket serverConnect = new ServerSocket(PORT)) {
				System.out.println(
						new Date() + ": Server started.\nListening for connections on port : " + PORT + " ...\n");

				// listen until user halts server execution
				while (true) {
					Socket socket = serverConnect.accept();
					work.getQueue().enqueue(socket);
					if (IS_VERBOSE) {
						System.out.println(new Date() + ": Connection opened");
					}
				}
			}
		} catch (IOException e) {
			System.err.println(new Date() + ": Server Connection error : " + e.getMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Usage: java [SQLITE JAR CLASSPATH] URLShortner.java [PORT] [JDBC DB URL]");
		} catch (Exception e) {
			System.err.println(new Date() + e.getMessage());
		}
	}
}
