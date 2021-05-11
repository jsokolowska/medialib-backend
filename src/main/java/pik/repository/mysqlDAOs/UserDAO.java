package pik.repository.mysqlDAOs;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.HashMap;
import java.util.ResourceBundle;


import org.apache.commons.codec.binary.Hex;
import pik.repository.User;


public class UserDAO {

    private final String DRIVER_STRING = "com.mysql.cj.jdbc.Driver";
    private final Connection connection;

    public UserDAO() throws Exception {

        ResourceBundle bundle = ResourceBundle.getBundle("mysql");
        Class.forName(DRIVER_STRING);
        this.connection = DriverManager.getConnection(
                bundle.getString("connection_string"),
                bundle.getString("mysql-user"),
                bundle.getString("mysql-password")
        );
    }

    public boolean isPasswordMatch(String email, String password) throws Exception {

        String hashedFromDB = getPasswordHash(email);
        String saltFromDB = getSalt(email);
        HashMap<String, String> hashed = hashPassword(password, saltFromDB);
        String hashedPassword = hashed.get("passwHash");

        return hashedPassword.equals(hashedFromDB);
    }



    public boolean isUserExist(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);

        ResultSet rs = statement.executeQuery(queryString);

        return rs.next();
    }



    public User getAllUserInfo(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
        ResultSet rs = statement.executeQuery(queryString);

        String name = null;
        String surname = null;
        String userEmail = null;

        if (rs.next()) {
            name = rs.getString("first_name");
            surname = rs.getString("last_name");
            userEmail = rs.getString("email");
        }

        return new User(userEmail, name, surname);
    }


    public int getCount() throws Exception {
        Statement statement = this.connection.createStatement();
        String queryString = "SELECT COUNT(*) AS count FROM users";
        ResultSet rs = statement.executeQuery(queryString);

        int max = -1;

        if (rs.next()) {
            max = rs.getInt("count");
        }

        return max;
    }



    public String getSalt(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
        ResultSet rs = statement.executeQuery(queryString);

        String salt = null;

        if (rs.next()) {
            salt = rs.getString("seed");
        }

        return salt;
    }



    public String getUserName(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
        ResultSet rs = statement.executeQuery(queryString);

        String username = null;

        if (rs.next()) {
            username = rs.getString("first_name");
        }
        return username;
    }



    public String getLastName(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
        ResultSet rs = statement.executeQuery(queryString);

        String lastName = null;
        if (rs.next()) {
            lastName = rs.getString("last_name");
        }
        return lastName;
    }



    public HashMap<String, String> hashPassword(String password, String hexSalt) throws Exception {

        byte[] byteSalt = Hex.decodeHex(hexSalt.toCharArray());

        KeySpec spec = new PBEKeySpec(password.toCharArray(), byteSalt, 1000, 512);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        String strHash = Hex.encodeHexString(hash);
        String strSalt = Hex.encodeHexString(byteSalt);

        HashMap<String, String> passwStuff = new HashMap<>();
        passwStuff.put("passwHash", strHash);
        passwStuff.put("salt", strSalt);

        return passwStuff;
    }




    public void insertUser(String firstName, String lastName, String email, String password) throws Exception {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        String ssalt = Hex.encodeHexString(salt);

        HashMap<String, String> passwStuff = hashPassword(password, ssalt);
        String passwordHash = passwStuff.get("passwHash");
        String strSalt = passwStuff.get("salt");


        String queryString = "INSERT INTO users(first_name, last_name, email, password_hash, seed) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setString(3, email);
        statement.setString(4, passwordHash);
        statement.setString(5, strSalt);

        statement.execute();
        statement.executeQuery("commit;");
    }




    public String getPasswordHash(String email) throws Exception {

        Statement statement = this.connection.createStatement();
        String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
        ResultSet rs = statement.executeQuery(queryString);

        String hash = null;

        if (rs.next()) {
            hash = rs.getString("password_hash");
        }
        return hash;
    }


    public void modifyUserEmail(String email, String newEmail) throws Exception {
        String queryString = "UPDATE users SET email = ? WHERE email = ?";

        PreparedStatement statement = this.connection.prepareStatement(queryString);
        statement.setString(1, newEmail);
        statement.setString(2, email);

        statement.execute();
    }

    public void modifyAllData(String email, String name, String surname, String newEmail) throws Exception {

        String queryString = "UPDATE users SET email = ?, first_name = ?, last_name = ? WHERE email = ?";

        PreparedStatement statement = this.connection.prepareStatement(queryString);
        statement.setString(1, newEmail);
        statement.setString(2, name);
        statement.setString(3, surname);
        statement.setString(4, email);

        statement.execute();

    }



    public void deleteUser(String email) throws Exception {

        String queryString = String.format("DELETE FROM users WHERE email = \"%s\"", email);
        Statement statement = this.connection.createStatement();
        statement.execute(queryString);
        statement.execute("commit;");
    }



    public void close() throws Exception {
        this.connection.close();
    }

}