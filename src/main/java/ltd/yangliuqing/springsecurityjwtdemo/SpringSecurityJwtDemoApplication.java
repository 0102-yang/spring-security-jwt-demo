package ltd.yangliuqing.springsecurityjwtdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author yang
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringSecurityJwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJwtDemoApplication.class, args);
    }

}
