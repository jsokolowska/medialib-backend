package pik.repository;

import java.util.Map;
import java.util.TreeMap;

public class LoginUsers {
    private Map<String, String> loginUser = new TreeMap<>();
    public String getToken(String email){
        String token = loginUser.get(email);
        if(token==null){
            return "";
        }
        return token;
    }
    public boolean checkUser(String email, String token){
        String userToken = loginUser.get(email);
        if(userToken==null || !userToken.equals(token)){
            return false;
        }
        return true;
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
