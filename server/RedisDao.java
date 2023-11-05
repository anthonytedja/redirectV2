//package server;

import redis.clients.jedis.JedisPooled;

public class RedisDao {
	private JedisPooled jedis;

	public RedisDao(String host, int port) {
		this.jedis = new JedisPooled(host, port);
	}

	public String get(String key) {
		return this.jedis.get(key);
	}

	public void set(String key, String value) {
		this.jedis.set(key, value);
	}
	
}
