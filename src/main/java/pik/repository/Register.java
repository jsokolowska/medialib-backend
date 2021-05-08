package pik.repository;

public class Register {
    private String email;
    private String password;
    private String name;
    private String surname;

    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public String getName(){
        return this.name;
    }
    public String getSurname(){
        return this.surname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setName(String name){
        this.name = name;
    }
    public  void setSurname(String surname){
        this.surname = surname;
    }
}
