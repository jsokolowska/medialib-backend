package pik.repository.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.security.config.Elements.JWT;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String HEADER_TOKEN = "X-API-TOKEN";
    private static final String KEY = "pikKey";

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(authentication == null){
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String tokenJwt = request.getHeader(HEADER_TOKEN);
        if(tokenJwt == null) return null;
        Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(tokenJwt).getBody();
        String login = claims.getSubject();
        if(login == null) return null;
        return new UsernamePasswordAuthenticationToken(login, null, );
    }

}
