import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

/**
 * @Author Luffy5522
 * @date: 2023/2/11 21:54
 * @description:
 */
@Slf4j
public class JedisTest {

    private Jedis jedis;

    @BeforeEach
    void setUp() {

        // 建立连接
        Jedis jedis = new Jedis("192.168.10.102", 6379);

        // 设置密码
        jedis.auth("123456");

        // 选择库
        jedis.select(0);

    }

    @Test
    void testString() {

        // 建立连接
        Jedis jedis = new Jedis("192.168.10.102", 6379);

        // 设置密码
        jedis.auth("123456");

        // 选择库
        jedis.select(0);

        String set = jedis.set("name", "张三");
        System.out.println(set);
        String name = jedis.get("name");
        System.out.println( "name:"+name);
    }

    @AfterEach
    void setDown() {
        if (jedis != null)
            jedis.close();
    }


}
