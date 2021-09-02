package ltd.yangliuqing.springsecurityjwtdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import ltd.yangliuqing.springsecurityjwtdemo.service.authentication.UuidWebTokenAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.InvocationTargetException;

@SpringBootTest
public class ObjectMapperTest {

    @Autowired
    private UuidWebTokenAuthenticationFilter filter;

    @Autowired
    private StringRedisTemplate template;

    @Test
    void test() throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String rawJson = this.template.opsForValue().get("1dd1dce9-c081-40a8-9efd-fef3e04b35e2");
        var method = this.filter.getClass().getDeclaredMethod("parseUser", String.class);
        method.setAccessible(true);
        var res = (User) method.invoke(this.filter, rawJson);
        System.out.println(res.toString());
    }

}
