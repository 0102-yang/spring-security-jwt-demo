package ltd.yangliuqing.springsecurityjwtdemo.config;

import lombok.extern.slf4j.Slf4j;
import ltd.yangliuqing.springsecurityjwtdemo.service.authentication.UuidWebTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author yang
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private LoginSuccessfulHandler successfulHandler;

    @Autowired
    private UuidWebTokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        var user1 = User.builder().username("Yang").password(this.passwordEncoder.encode("123456")).roles("USER", "ADMIN");
        auth.inMemoryAuthentication().passwordEncoder(this.passwordEncoder).withUser(user1);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用Session和CSRF
        http.sessionManagement().disable();
        http.csrf().disable();
        http.rememberMe().disable();
        http.httpBasic().disable();

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setContentType("text/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter printWriter = response.getWriter();
            printWriter.write(authException.getMessage());
            printWriter.close();
        });

//        http.formLogin().loginProcessingUrl("/login").permitAll().failureHandler((request, response, exception) -> {
//            response.setContentType("application/json;charset=UTF-8");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//            TreeMap<String, Object> values = new TreeMap<>();
//            values.put("message", exception.getMessage());
//
//            PrintWriter printWriter = response.getWriter();
//            printWriter.write(this.mapper.writeValueAsString(values));
//            printWriter.close();
//        });

        http.formLogin().successHandler(this.successfulHandler).failureHandler((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/json;charset=UTF-8");

            PrintWriter printWriter = response.getWriter();
            printWriter.write("Login fail");
            printWriter.close();
        });

        // 配置路径
        http.authorizeRequests().antMatchers("/authentication").authenticated().antMatchers("/permit").permitAll();

        // 添加过滤器
        http.addFilterAfter(this.tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
