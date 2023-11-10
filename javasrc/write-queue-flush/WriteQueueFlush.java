import common.RedisDao;
import common.CassandraDao;
import common.KeyValuePair;

public class WriteQueueFlush {
    public static void main(String[] args) {
        RedisDao redis = new RedisDao("write-queue", 6379);
        CassandraDao cassandra = new CassandraDao("idk", 1234);

        while (true) {
            KeyValuePair keyValuePair = redis.blockPop();
            cassandra.set(keyValuePair.key, keyValuePair.value);
        }
    }   
}