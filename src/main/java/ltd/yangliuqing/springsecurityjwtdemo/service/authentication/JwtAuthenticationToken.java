package ltd.yangliuqing.springsecurityjwtdemo.service.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.User;

/**
 * @author yang
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final User principal;

    private final String token;

    public JwtAuthenticationToken(User principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.token = null;
    }

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

}
