package ltd.yangliuqing.springsecurityjwtdemo.service.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.jsonwebtoken.Claims;
import ltd.yangliuqing.springsecurityjwtdemo.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author yang
 */
@Component
public class JwtWebTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        Optional<Claims> claims = JwtUtils.getClaims(token);

        if (claims.isPresent()) {
            String username = claims.get().getSubject();
            String rawUserJson = this.stringRedisTemplate.opsForValue().get(username);
            User user = parseUser(rawUserJson);

            // 验证成功,填充已被认证的证书
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(user);
            authenticationToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private User parseUser(@Nullable String rawUserJson) throws JsonProcessingException {
        rawUserJson = (rawUserJson != null) ? rawUserJson : "";

        // 从Json字符串获取用户信息
        JsonNode fields = this.mapper.readTree(rawUserJson);
        BooleanNode accountNonExpired = (BooleanNode) fields.get("accountNonExpired");
        BooleanNode accountNonLocked = (BooleanNode) fields.get("accountNonLocked");
        BooleanNode enabled = (BooleanNode) fields.get("enabled");
        BooleanNode credentialsNonExpired = (BooleanNode) fields.get("credentialsNonExpired");
        TextNode username = (TextNode) fields.get("username");

        ArrayNode authoritiesNode = (ArrayNode) fields.get("authorities");
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        for (JsonNode e : authoritiesNode) {
            String authorityString = e.get("authority").textValue();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityString);
            authorities.add(authority);
        }

        return new User(username.asText(), "", enabled.asBoolean(), accountNonExpired.asBoolean(), credentialsNonExpired.asBoolean(), accountNonLocked.asBoolean(), authorities);
    }

}
