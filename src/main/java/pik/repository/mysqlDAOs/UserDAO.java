package pik.repository.mysqlDAOs;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.HashMap;
import java.util.ResourceBundle;


import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import pik.repository.User;


public class UserDAO {

    private final String DRIVER_STRING = "com.mysql.cj.jdbc.Driver";
    private Connection connection;

    public UserDAO() {

        ResourceBundle bundle = ResourceBundle.getBundle("mysql");

        try {
            Class.forName(DRIVER_STRING);
            this.connection = DriverManager.getConnection(
                    bundle.getString("connection_string"),
                    bundle.getString("mysql-user"),
                    bundle.getString("mysql-password")
            );
        } catch (Exception e) {
            System.out.println("Can't connect to the database");
            System.out.println(e.getMessage());
        }
    }

    public boolean isPasswordMatch(String email, String password) {
        String hashedFromDB = null;
        String saltFromDB = null;
        HashMap<String,String> hashed;

        String hashedPassword = null;
        boolean passwordsEqual = false;

        try {
            hashedFromDB = getPasswordHash(email);
            saltFromDB = getSalt(email);
            hashed = hashPassword(password, saltFromDB);
            hashedPassword = hashed.get("passwHash");

            passwordsEqual = hashedPassword.equals(hashedFromDB);
        } catch (Exception e) {
            System.out.println("An error occured while comparing passwords");
            System.out.println(e.getMessage());
        }
        return passwordsEqual;
    }



    public boolean isUserExist(String email) {

        boolean exists = false;
        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);

            ResultSet rs = statement.executeQuery(queryString);
            exists = rs.next();
        } catch (Exception e) {
            System.out.println("An error getting user from the database");
            System.out.println(e.getMessage());
        }

        return exists;
    }



    public User getAllUserInfo(String email) {


        String name = null;
        String surname = null;
        String userEmail = null;

        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
            ResultSet rs = statement.executeQuery(queryString);


            if (rs.next()) {
                name = rs.getString("first_name");
                surname = rs.getString("last_name");
                userEmail = rs.getString("email");
            }
        } catch (Exception e) {
            System.out.println("An error getting user info from database");
            System.out.println(e.getMessage());
        }


        return new User(userEmail, name, surname);
    }


    public int getCount()  {

        int max = -1;

        try {
            Statement statement = this.connection.createStatement();
            String queryString = "SELECT COUNT(*) AS count FROM users";
            ResultSet rs = statement.executeQuery(queryString);


            if (rs.next()) {
                max = rs.getInt("count");
            }
        } catch (Exception e) {
            System.out.println("Error counting users");
            System.out.println(e.getMessage());
        }

        return max;
    }



    public String getSalt(String email) {

        String salt = null;

        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
            ResultSet rs = statement.executeQuery(queryString);

            if (rs.next()) {
                salt = rs.getString("seed");
            }
        } catch (Exception e) {
            System.out.println("An error getting salt from the user");
            System.out.println(e.getMessage());
        }

        return salt;
    }



    public String getUserName(String email) throws Exception {

        String username = null;
        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
            ResultSet rs = statement.executeQuery(queryString);


            if (rs.next()) {
                username = rs.getString("first_name");
            }
        } catch (Exception e) {
            System.out.println("Error getting the name of the user");
            System.out.println(e.getMessage());
        }
        return username;
    }



    public String getLastName(String email) throws Exception {

        String lastName = null;

        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
            ResultSet rs = statement.executeQuery(queryString);


            if (rs.next()) {
                lastName = rs.getString("last_name");
            }
        } catch (Exception e) {
            System.out.println("Error getting user's last name");
            System.out.println(e.getMessage());
        }
        return lastName;
    }



    public HashMap<String, String> hashPassword(String password, String hexSalt) {

        String strSalt = null;
        String strHash = null;
        HashMap<String, String> passwStuff;
        byte[] byteSalt = null;
        byte[] hash = null;

        try {
            byteSalt = Hex.decodeHex(hexSalt.toCharArray());

            KeySpec spec = new PBEKeySpec(password.toCharArray(), byteSalt, 1000, 512);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            hash = factory.generateSecret(spec).getEncoded();
        } catch (DecoderException decExc) {
            System.out.println("Error decoding salt");
            System.out.println(decExc.getMessage());
        } catch (NoSuchAlgorithmException noAlgoExc) {
            System.out.println("Error finding hashing algorithm");
            System.out.println(noAlgoExc.getMessage());
        } catch (InvalidKeySpecException invalidKeyExc) {
            System.out.println("Error finding key");
            System.out.println(invalidKeyExc.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        strHash = Hex.encodeHexString(hash);
        strSalt = Hex.encodeHexString(byteSalt);

        passwStuff = new HashMap<>();
        passwStuff.put("passwHash", strHash);
        passwStuff.put("salt", strSalt);

        return passwStuff;
    }




    public void insertUser(String firstName, String lastName, String email, String password) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        String ssalt = Hex.encodeHexString(salt);

        HashMap<String, String> passwStuff = hashPassword(password, ssalt);
        String passwordHash = passwStuff.get("passwHash");
        String strSalt = passwStuff.get("salt");

        try {

            String queryString = "INSERT INTO users(first_name, last_name, email, password_hash, seed) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, passwordHash);
            statement.setString(5, strSalt);

            statement.execute();
            statement.executeQuery("commit;");
        } catch (SQLException ex) {
            System.out.println("An exception while inserting user occured");
            System.out.println(ex.getMessage());
        }
    }




    public String getPasswordHash(String email) {

        String hash = null;
        try {
            Statement statement = this.connection.createStatement();
            String queryString = String.format("SELECT * FROM users WHERE email = \"%s\"", email);
            ResultSet rs = statement.executeQuery(queryString);


            if (rs.next()) {
                hash = rs.getString("password_hash");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hash;
    }


    public void modifyUserEmail(String email, String newEmail) {

        try {
            String queryString = "UPDATE users SET email = ? WHERE email = ?";

            PreparedStatement statement = this.connection.prepareStatement(queryString);
            statement.setString(1, newEmail);
            statement.setString(2, email);

            statement.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void modifyAllData(String email, String name, String surname, String newEmail) {

        try {
            String queryString = "UPDATE users SET email = ?, first_name = ?, last_name = ? WHERE email = ?";

            PreparedStatement statement = this.connection.prepareStatement(queryString);
            statement.setString(1, newEmail);
            statement.setString(2, name);
            statement.setString(3, surname);
            statement.setString(4, email);

            statement.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }



    public void deleteUser(String email) {

        try {

            String queryString = String.format("DELETE FROM users WHERE email = \"%s\"", email);
            Statement statement = this.connection.createStatement();
            statement.execute(queryString);
            statement.execute("commit;");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }



    public void close() {
        try {
            this.connection.close();
        } catch (Exception ex) {
            System.out.println("Closing connection exception");
            System.out.println(ex.getMessage());
        }
    }

}