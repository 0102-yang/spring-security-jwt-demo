package ltd.yangliuqing.springsecurityjwtdemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 * @author yang
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    public LoginFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        TreeMap<String, Object> values = new TreeMap<>();
        values.put("message", exception.getMessage());

        PrintWriter printWriter = response.getWriter();
        printWriter.write(this.mapper.writeValueAsString(values));
        printWriter.close();
    }

}
