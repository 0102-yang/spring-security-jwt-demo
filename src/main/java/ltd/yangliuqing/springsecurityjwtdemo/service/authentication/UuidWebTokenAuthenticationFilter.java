package ltd.yangliuqing.springsecurityjwtdemo.service.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

/**
 * @author yang
 */
@Component
public class UuidWebTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        token = (token != null) ? token : "";
        String rawUserJson = this.stringRedisTemplate.opsForValue().get(token);
        if (rawUserJson != null) {
            User user = parseUser(rawUserJson);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private User parseUser(String rawUserJson) throws JsonProcessingException {
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
