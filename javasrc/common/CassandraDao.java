package common;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.net.InetSocketAddress;
import java.util.Map;

public class CassandraDao {
	private String host;
	private int port;
	private CqlSession session;

	private String KEYSPACE = "url_shortener";

	public CassandraDao(String host, int port) {
		this.host = host;
		this.port = port;

		this.host = "cassandra";
		this.port = 9042;
		// cassandra, 9042
		//connect(host, port);
	}

	/*
	private void connect(String host, int port) {
		CqlSessionBuilder builder = CqlSession.builder();
		builder.addContactPoint(new InetSocketAddress(host, port));
		builder.withKeyspace(CqlIdentifier.fromCql(KEYSPACE));

		this.session = builder.build();
	}
	*/

	private CqlSession getSession() {
		return this.session;
	}

	private void close() {
		this.session.close();
	}

	private void testRequest() {
		try {
			ResultSet result = this.session.execute("SELECT release_version from system.local");
			Row row = result.one();
			System.out.println(row.getString("release_version"));
		} catch (Exception e) {
			System.out.println(e);
		}
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
