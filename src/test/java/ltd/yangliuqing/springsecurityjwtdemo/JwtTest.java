package ltd.yangliuqing.springsecurityjwtdemo;

import ltd.yangliuqing.springsecurityjwtdemo.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class JwtTest {

    @Test
    void test() {
        String subject = "Yang";
        String token = JwtUtils.createToken(subject, 7, TimeUnit.HOURS);
        System.out.println(token);

        var claims = JwtUtils.getClaims(token);
        claims.ifPresent(System.out::println);
    }

    @Test
    void testJwt() {

    }

}
