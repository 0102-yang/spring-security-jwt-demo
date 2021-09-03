package ltd.yangliuqing.springsecurityjwtdemo.config;

import lombok.extern.slf4j.Slf4j;
import ltd.yangliuqing.springsecurityjwtdemo.handler.AuthenticationEntryPointImpl;
import ltd.yangliuqing.springsecurityjwtdemo.handler.LoginFailureHandler;
import ltd.yangliuqing.springsecurityjwtdemo.handler.LoginSuccessfulHandler;
import ltd.yangliuqing.springsecurityjwtdemo.service.authentication.JwtWebTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    private LoginFailureHandler failureHandler;

    @Autowired
    private AuthenticationEntryPointImpl entryPoint;

    @Autowired
    private JwtWebTokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        var user1 = User.builder().username("yang").password(this.passwordEncoder.encode("123456")).roles("USER", "ADMIN");
        var user2 = User.builder().username("root").password(this.passwordEncoder.encode("123456")).roles("USER");
        auth.inMemoryAuthentication().passwordEncoder(this.passwordEncoder).withUser(user1).withUser(user2);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用Session和CSRF
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        http.rememberMe().disable();

        http.exceptionHandling().authenticationEntryPoint(this.entryPoint);

        // 配置登录
        http.formLogin().permitAll().successHandler(this.successfulHandler).failureHandler(this.failureHandler);
        
        // 配置路径
        http.authorizeRequests().antMatchers("/user").hasAnyRole("USER", "ADMIN").antMatchers("/admin").hasRole("ADMIN").antMatchers("/permit").permitAll();

        // 添加过滤器
        http.addFilterAfter(this.tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
