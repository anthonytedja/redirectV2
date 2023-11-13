import common.RedisDao;
import common.CassandraDao;
import common.KeyValuePair;

public class WriteQueueFlush {
    static String REDIS_HOSTNAME;
    static int REDIS_PORT;
    static String CASSANDRA_HOSTNAME;
    static int CASSANDRA_PORT;
    static String CASSANDRA_KEYSPACE;
    static String CASSANDRA_DATACENTER;

    public static void main(String[] args) {
        try {
            REDIS_HOSTNAME = args[0];
            REDIS_PORT = Integer.parseInt(args[1]);
            CASSANDRA_HOSTNAME = args[2];
            CASSANDRA_PORT = Integer.parseInt(args[3]);
            CASSANDRA_KEYSPACE = args[4];
            CASSANDRA_DATACENTER = args[5];

            RedisDao redis = new RedisDao(REDIS_HOSTNAME, REDIS_PORT);
            CassandraDao cassandra = new CassandraDao(CASSANDRA_HOSTNAME, CASSANDRA_PORT, CASSANDRA_KEYSPACE,
                    CASSANDRA_DATACENTER);

            while (true) {
                System.out.println("Waiting to pop from queue");
                KeyValuePair keyValuePair = redis.blockPop();
                System.out.println(keyValuePair.toString());
                cassandra.set(keyValuePair.key, keyValuePair.value);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}