package pik.repository;

public class User {
    private String token;
    private String email;
    private String password;
    private String name;
    private String surname;

    public User(String email, String password, String name, String surname){
      this.token = "";
      this.email = email;
      this.password = password;
      this.name = name;
      this.surname = surname;
    }

    public User(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public void setToken(String token){
        this.token = token;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password =password;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getToken(){
        return this.token;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getName(){
        return this.name;
    }

    public  String getSurname(){
        return this.surname;
    }
}
