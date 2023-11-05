//package server;

import java.util.Map;

public class CassandraDao {
	private String dbPath;

	public CassandraDao(String dbPath) {
		this.dbPath = dbPath;
	}

	public String get(String shortUrl) {
		return "https://google.com";
	}

	public void set(String shortUrl, String longUrl) {
		return;
	}

	public void setBatch(Map<String, String> shortToLong) {
		return;
	}
}
