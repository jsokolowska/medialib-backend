package pik.repository;

public class User {
    private String token = "token";
    private String email;
    private int hash;
    private String name;
    private String surname;

    public User(String email, int hash, String name, String surname){
      this.token = "";
      this.email = email;
      this.hash = hash;
      this.name = name;
      this.surname = surname;
    };

    public void setToken(String token){
        this.token = token;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setHash(int hash){
        this.hash =hash;
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

    public int getHash(){
        return this.hash;
    }

    public String getName(){
        return this.name;
    }

    public  String getSurname(){
        return this.surname;
    }
}
