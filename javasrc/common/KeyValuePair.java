package common;

import redis.clients.jedis.util.KeyValue;
import java.util.List;

public class KeyValuePair {
    public String key;
    public String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    static public KeyValuePair fromString(String raw) {
        String[] split = raw.split("[->]");
        return new KeyValuePair(split[0], split[1]);
    }

    public String toString() {
        return this.key + "[->]" + this.value;
    }
}