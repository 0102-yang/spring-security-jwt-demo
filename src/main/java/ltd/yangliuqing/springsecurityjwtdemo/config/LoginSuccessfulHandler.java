package ltd.yangliuqing.springsecurityjwtdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yang
 */
@Component
public class LoginSuccessfulHandler implements AuthenticationSuccessHandler {

    private final StringRedisTemplate template;

    private final ObjectMapper mapper;

    public LoginSuccessfulHandler(StringRedisTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ValueOperations<String, String> valueOps = this.template.opsForValue();
        String uuid = UUID.randomUUID().toString();
        valueOps.set(uuid, this.mapper.writeValueAsString(authentication.getPrincipal()), 20, TimeUnit.MINUTES);

        TreeMap<String, Object> values = new TreeMap<>();
        values.put("message", "Login success");
        values.put("user", authentication.getPrincipal());
        values.put("token", uuid);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(this.mapper.writeValueAsString(values));
        printWriter.flush();
        printWriter.close();
    }

}
