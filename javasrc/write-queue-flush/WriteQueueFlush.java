import common.RedisDao;
import common.CassandraDao;
import common.KeyValuePair;

public class WriteQueueFlush {
    static String REDIS_READ_MASTER_HOSTNAME;
	static String REDIS_READ_SLAVE_HOSTNAME;
	static int REDIS_PORT;
    static String CASSANDRA_HOSTNAME;
	static int CASSANDRA_PORT;

    public static void main(String[] args) {
        REDIS_READ_MASTER_HOSTNAME = args[0];
        REDIS_READ_SLAVE_HOSTNAME = args[1];
        REDIS_PORT = Integer.parseInt(args[2]);
        CASSANDRA_HOSTNAME = args[3];
		CASSANDRA_PORT = Integer.parseInt(args[4]);

        RedisDao redis = new RedisDao(REDIS_READ_MASTER_HOSTNAME, REDIS_READ_SLAVE_HOSTNAME, REDIS_PORT);
        CassandraDao cassandra = new CassandraDao(CASSANDRA_HOSTNAME, CASSANDRA_PORT);

        while (true) {
            KeyValuePair keyValuePair = redis.blockPop();
            cassandra.set(keyValuePair.key, keyValuePair.value);
        }
    }   
}