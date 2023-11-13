//package shortner;

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

	private SocketQueue queue;
	private Cache cache;
	private WriteBuffer writeBUffer;
	private CassandraDao dao;

	public ThreadWork(RedisDao redisDao, RedisDao bufferDao, CassandraDao cassDao) {
		this.queue = new SocketQueue();
		this.cache = new Cache(redisDao);
		this.writeBUffer = new WriteBuffer(bufferDao);
		this.dao = cassDao;
	}

	public SocketQueue getQueue() {
		return this.queue;
	}

	public Cache getCache() {
		return this.cache;
	}

	public WriteBuffer getWriteBuffer() {
		return this.writeBUffer;
	}

	public CassandraDao getUrlDao() {
		return this.dao;
	}
}