import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uva on 7/4/17.
 */
public class RedisSentinelFailoverTest {
    public static void main(String[] args) {
        GenericObjectPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(100);

        Set<String> sentinels = new HashSet<>(3);
        sentinels.add(new HostAndPort("127.0.0.1", 26379).toString());
        JedisSentinelPool pool = new JedisSentinelPool("bm-master", sentinels, jedisPoolConfig);
        for (long i = 0; i < 10000000000L; i++) {
//            As long as we use pool.getResource if any failover happens we would be able to resume our redis operations with new master.
            try (Jedis jedis = pool.getResource()) {

                try {
//                    System.out.println(jedis.info("Replication"));
                    jedis.incr("foo");
//                    throw ConnectException();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            } catch (JedisConnectionException e) {
                System.out.println("Master Down!!");
                System.out.println(e.getMessage());
            }
        }


    }
}
