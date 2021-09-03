package ltd.yangliuqing.springsecurityjwtdemo;

import ltd.yangliuqing.springsecurityjwtdemo.service.authentication.JwtWebTokenAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class ObjectMapperTest {

    @Autowired
    private JwtWebTokenAuthenticationFilter filter;

    @Autowired
    private StringRedisTemplate template;

    @Test
    void test() {
        var s = this.template.opsForValue().get("sdjkfasdkfkjdks");
        System.out.println(s);
    }

}
