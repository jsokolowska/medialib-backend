package pik.repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class Login {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;

    public Login(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }
}
