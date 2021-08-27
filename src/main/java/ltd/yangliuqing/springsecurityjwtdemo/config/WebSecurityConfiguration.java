package ltd.yangliuqing.springsecurityjwtdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 * @author yang
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(null).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用Session和CSRF
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();

        // 配置路径
        http.authorizeRequests().antMatchers("/login").anonymous();

        // 配置Entrypoint
        http.httpBasic()
                .authenticationEntryPoint(
                        ((request, response, authException) -> {
                            response.setContentType("text/json;charset=utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            PrintWriter printWriter = response.getWriter();

                            String message = "需要进行登录";
                            printWriter.write(message);
                            printWriter.flush();
                            printWriter.close();
                        }));

        // 配置登录
        http.formLogin()
                .successHandler(
                        (request, response, authentication) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.setStatus(HttpServletResponse.SC_OK);

                            TreeMap<String, Object> map = new TreeMap<>();
                            map.put("message", "登录成功");
                            map.put("user", authentication.getPrincipal());

                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(mapper.writeValueAsString(map));
                            printWriter.flush();
                            printWriter.close();
                        })
                .failureHandler(
                        (request, response, exception) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            TreeMap<String, Object> map = new TreeMap<>();
                            map.put("message", "登录失败");

                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(mapper.writeValueAsString(map));
                            printWriter.flush();
                            printWriter.close();
                        });
    }
}
