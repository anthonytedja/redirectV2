// package server;
import common.RedisDao;
import common.KeyValuePair;

public class Cache {
	private RedisDao dao;

	public Cache(RedisDao dao) {
		this.dao = dao;
	}

	public String get(String key) {
		return this.dao.get(key);
	}

	public void set(String key, String value) {
		this.dao.set(new KeyValuePair(key, value));
	}
}
