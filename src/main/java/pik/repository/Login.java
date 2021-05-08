package pik.repository;

public class Login {
    private String email;
    private int hash;

    public Login(String email, int hash){
        this.email = email;
        this.hash = hash;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setHash(int hash){
        this.hash = hash;
    }

    public String getEmail(){
        return this.email;
    }

    public int getHash(){
        return this.hash;
    }
}
