package common;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

public class CassandraDao {
	private String[] hosts;
	private int port;
	private String keyspace;
	private String datacenter;

	private CqlSession session;

	public CassandraDao(String hosts, int port, String keyspace, String datacenter) {
		this.hosts = hosts.split(",");
		this.port = port;
		this.keyspace = keyspace;
		this.datacenter = datacenter;

		connect();
	}

	private void connect() {
		CqlSessionBuilder builder = CqlSession.builder();
		for (String host : hosts) {
			builder.addContactPoint(new InetSocketAddress(host, this.port));
		}
		builder.withLocalDatacenter(this.datacenter);
		builder.withKeyspace(CqlIdentifier.fromCql(this.keyspace));

		this.session = builder.build();
	}

	private CqlSession getSession() {
		return this.session;
	}

	private void close() {
		this.session.close();
	}

	public void getVersion() {
		try {
			ResultSet result = this.session.execute("SELECT release_version from system.local");
			Row row = result.one();
			System.out.println(row.getString("release_version"));
		} catch (Exception e) {
			System.out.println(new Date() + ": " + e);
		}
	}

	public String get(String shortUrl) {
		try {
			PreparedStatement preparedStatement = this.session
					.prepare("SELECT url_original from url_shortener.urls WHERE short_code = ?");
			BoundStatement boundStatement = preparedStatement.bind(shortUrl);
			ResultSet result = this.session.execute(boundStatement);

			Row row = result.one();
			if (row == null) {
				return null;
			} else {
				return row.getString("url_original");
			}
		} catch (Exception e) {
			System.out.println(new Date() + ": " +  e);
			return null;
		}
	}

	public void set(String shortUrl, String longUrl) {
		try {
			PreparedStatement preparedStatement = this.session
					.prepare("INSERT INTO url_shortener.urls (short_code, url_original) VALUES (?, ?)");
			BoundStatement boundStatement = preparedStatement.bind(shortUrl, longUrl);
			this.session.execute(boundStatement);
		} catch (Exception e) {
			System.out.println(new Date() + ": " + e);
		}
	}
}
