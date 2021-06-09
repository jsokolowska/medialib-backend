package pik.repository.oauth;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class LoginUsers{
    private final Map<String, String> loginUser = new TreeMap<>();

    public String getToken(String email){
        String token = loginUser.get(email);
        if(token==null){
            return "";
        }
        return token;
    }
    public boolean checkUser(String email, String token){
        String userToken = loginUser.get(email);
        return userToken != null && userToken.equals(token);
    }

    public boolean checkUser(String email){
        String userToken = loginUser.get(email);
        return userToken != null;
    }

    public String addUser(String email){
        String token = generateToken(email);
        loginUser.put(email, token);
        return token;
    }
    public void deleteUser(String email){
        loginUser.remove(email);
    }
    private String generateToken(String email){
        return "token:"+email;
    }
}
