package common;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.JedisPooled;
import common.KeyValuePair;

import java.util.Arrays;
import java.util.List;

public class RedisDao {
	private String masterHost;
	private String slaveHost;
	private int port;
	private JedisPooled jedis;

	public RedisDao(String masterHost, int port) throws JedisConnectionException {
		this.masterHost = masterHost;
		this.slaveHost = null;
		this.port = port;

		connect();
	}

	public RedisDao(String masterHost, String slaveHost, int port) throws JedisConnectionException {
		this.masterHost = masterHost;
		this.slaveHost = slaveHost;
		this.port = port;

		connect();
	}

	public void connect() {
		try {
			this.jedis = new JedisPooled(this.masterHost, this.port);
		} catch (JedisConnectionException e) {
			this.jedis = new JedisPooled(this.slaveHost, this.port);
		}
	}

	public String get(String key) {
		return this.jedis.get(key);
	}

	public void set(KeyValuePair keyValuePair) {
		System.out.println("setting a key: " + keyValuePair.toString());
		this.jedis.set(keyValuePair.key, keyValuePair.value);
	}

	public void push(KeyValuePair keyValuePair) {
		System.out.println("pushing to writequeuelist: " + keyValuePair.toString());
		this.jedis.lpush("writequeuelist", keyValuePair.toString());
	}

	public KeyValuePair blockPop() {
		// timeout = 0; wait forever
		List<String> result = this.jedis.brpop(0, "writequeuelist");
		return KeyValuePair.fromString(result.get(0));
	}
}
