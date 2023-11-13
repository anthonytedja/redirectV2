//package shortner;

import common.RedisDao;
import common.KeyValuePair;

public class WriteBuffer {
	private RedisDao dao;

	public WriteBuffer(RedisDao dao) {
		this.dao = dao;
	}

	public void set(String key, String value) {
		this.dao.push(new KeyValuePair(key, value));
	}
}
