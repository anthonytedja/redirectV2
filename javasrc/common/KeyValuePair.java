package common;
import redis.clients.jedis.util.KeyValue;
import java.util.List;

public class KeyValuePair  {
    public String key;
    public String value;
    
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    static public KeyValuePair fromListBytes(List<byte[]> list) {
        String key = list.get(0).toString();
        String value = list.get(1).toString();
        return new KeyValuePair(key, value);
    }
}