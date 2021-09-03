package ltd.yangliuqing.springsecurityjwtdemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltd.yangliuqing.springsecurityjwtdemo.util.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 获取用户以及创建token
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        long expiredDays = 7;
        String token = JwtUtils.createToken(username, expiredDays, TimeUnit.DAYS);

        // 将用户信息缓存起来,有效期7天
        this.template.opsForValue().set(username, this.mapper.writeValueAsString(user), expiredDays, TimeUnit.DAYS);

        TreeMap<String, Object> values = new TreeMap<>();
        values.put("message", "Login success");
        values.put("user", authentication.getPrincipal());
        values.put("token", token);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(this.mapper.writeValueAsString(values));
        printWriter.close();
    }

}
