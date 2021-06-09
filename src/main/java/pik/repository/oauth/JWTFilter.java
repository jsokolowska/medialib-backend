package pik.repository.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JWTFilter extends GenericFilterBean {

    private static final String HEADER_TOKEN = "X-API-TOKEN";
    private String key;

    public JWTFilter(String keyJwt){
        key = keyJwt;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String tokenJwt = httpRequest.getHeader(HEADER_TOKEN);
        if(tokenJwt == null){
            throw new ServletException("Missing or invalid token");
        } else {
            try {
                Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(tokenJwt).getBody();
                String login = claims.getSubject();
                //String token =
                HttpServletRequest req = (HttpServletRequest) servletRequest;
                MutableHTTPServletRequest mutable_req = new MutableHTTPServletRequest(req);

                mutable_req.addHeader("LOGIN", login);
                filterChain.doFilter(mutable_req, servletResponse);
            }catch (final SignatureException e){
                throw new ServletException("Invalid token");
            }
        }
    }
}
