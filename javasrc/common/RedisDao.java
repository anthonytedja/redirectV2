package common;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.JedisPooled;
import common.KeyValuePair;
import java.util.List;

public class RedisDao {
	private String host;
	private int port;
	private JedisPooled jedis;

	public RedisDao(String host, int port) throws JedisConnectionException {
		this.host = host;
		this.port = port;

		try {
			this.jedis = new JedisPooled("redis-master", port);
		} catch (JedisConnectionException e) {
			this.jedis = new JedisPooled("redis-slave", port);
		}
	}

	public String get(String key) {
		return this.jedis.get(key);
	}

	public void set(KeyValuePair keyValuePair) {
		this.jedis.set(keyValuePair.key, keyValuePair.value);
	}

	public void push(KeyValuePair keyValuePair) {
		this.jedis.lpush(keyValuePair.key, keyValuePair.value);
	}

	public KeyValuePair blockPop() {
		// timeout = 0; wait forever
		List<byte[]> result = this.jedis.blpop(0, new byte[0]);
		return KeyValuePair.fromListBytes(result);
	}
}
