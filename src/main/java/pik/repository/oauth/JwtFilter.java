package pik.repository.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {

    private static final String HEADER_TOKEN = "X-API-TOKEN";
    private static final String KEY = "pikKey";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        String tokenJwt = request.getHeader(HEADER_TOKEN);
        if(tokenJwt == null){
            throw new IllegalArgumentException("Missing token");
        } else {
            try {
                Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(tokenJwt).getBody();
                String login = claims.getSubject();
                MutableHTTPServletRequest mutable_req = new MutableHTTPServletRequest(request);

                mutable_req.addHeader("LOGIN", login);
                //filterChain.doFilter(mutable_req, response);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, "");
                setDetails(mutable_req, token);
                return this.getAuthenticationManager().authenticate(token);
            } catch (final SignatureException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
}
