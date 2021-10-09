import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;


class Funcs{
    public String get_varchar(int[] arr){
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<arr.length-1; i++){
            sb.append(String.valueOf(arr[i])+" ");
        }
        sb.append(String.valueOf(arr[arr.length-1]));
        return sb.toString();
    }

    public int[] getback_arr(String s){
        String[] sarr = s.split(" ");
        int[] arr= new int[sarr.length];
        for (int i=0; i<arr.length; i++){
            arr[i] = Integer.parseInt(sarr[i]);
        }
        return arr;
    }
}
 
public class RSA_DB_Connection{

    private static HashMap<Character, Integer> map = new HashMap<>();
    public static void main (String [] arguments) throws IOException{

//------------------------------------------------------------
        // USERNAME and PASSWORD and ID values

        String username = "Tester";
        String password = "Neural Networks";
        int id = 40;


        // Pre-encryption 
        Funcs f = new Funcs(); // Object creation
        int i = 0;
        for (char c='a'; c<='z'; c++){
            map.put(c, i);
            i++;
        }
        map.put(' ', 26);
        map.put('.', 27);
        map.put(',', 28);
        map.put('-', 29);
        i+=5;
        for (char c='A'; c<='Z'; c++){
            map.put(c, i);
            i++;
        }

// ---------------------------------------------------------------------------
        // USERNAME - Process for creating the array to encrypt
        int enarr[] = new int[username.length()];


        // String to numbers
        for (int j=0; j<username.length(); j++){
            enarr[j] = map.get(username.charAt(j));
        }
        System.out.println("\nEncrypting the USERNAME....... ");
        enarr = encryptMessage(enarr);
        String encrypted_usern = f.get_varchar(enarr);


// ---------------------------------------------------------------------------
        // PASSWORD - Process for creating the array to encrypt
        int enarr1[] = new int[password.length()];


        // String to numbers
        for (int j=0; j<password.length(); j++){
            enarr1[j] = map.get(password.charAt(j));
        }
        System.out.println("\nEncrypting the PASSWORD....... ");
        enarr1 = encryptMessage(enarr1);
        String encrypted_pass = f.get_varchar(enarr1);

        // Database Connection
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connections
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DATABASE_NAME", "USERNAME", "PASSWORD");
            System.out.println("Connection successful.....");

            // Statement
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into `user_info` (user_id, username, pass) values ('"+id+"', '"+encrypted_usern+" ' , '"+encrypted_pass+"')");
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
        System.out.println("Database modified.........");
// ---------------------------------------------------------------------

        // FETCH VALUES from DATABASE
        String db_username="", db_pass="";
        int db_id=40;
        String query = "select username, pass from user_info where user_id = '"+db_id+"';";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connections
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/isaa_db", "Om", "Eternity42001");
            System.out.println("Connection successful.....");

            // Statement
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                db_username = rs.getString("username");
                db_pass = rs.getString("pass");              
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }

        // Get the int[] for fetched VALUES
        int[] fet_username = f.getback_arr(db_username);
        int[] fet_pass = f.getback_arr(db_pass);



        // Decryption initiation
        System.out.println("\n\nDecrypting credentials....... ");

        int[] dec_username = decryptMessage(fet_username);
        int[] dec_pass = decryptMessage(fet_pass);

        StringBuffer sb = new StringBuffer();
        for (int k=0; k<dec_username.length; k++){
            int val = dec_username[k];
            for (char key: map.keySet()){
                if (map.get(key)==val){
                    sb.append(key);
                    break;
                }
            }
        }
        System.out.println("\nFetched USERNAME-- "+sb.toString());

        sb.setLength(0);
        for (int k=0; k<dec_pass.length; k++){
            int val = dec_pass[k];
            for (char key: map.keySet()){
                if (map.get(key)==val){
                    sb.append(key);
                    break;
                }
            }
        }
        System.out.println("\nFetched PASSWORD-- "+sb.toString());

    }
 

// ------------------------------------------------------------------

    // INTERNAL FUNCTIONS
    // Encrypting the message
    public static int[] encryptMessage(int[] msg){
        BigInteger b = BigInteger.valueOf(77);
        BigInteger e = BigInteger.valueOf(13);
        for (int i=0; i<msg.length; i++){
            BigInteger val = BigInteger.valueOf(msg[i]);
            BigInteger s = val.modPow(e, b);
            msg[i] = s.intValue();
        }
        return msg;
    }
 
    // Decrypting the message
    public static int[] decryptMessage(int[] msg){
        int f = 0, k=1, d = 0;
        // Find d value
        while (f==0){
            if (((1+(k*60))%13)==0){
                d = ((1+(k*60))/13);
                f = 1;
            }else k+=1;
        }

        // Decrypt
        BigInteger de = BigInteger.valueOf(d);
        BigInteger n = BigInteger.valueOf(77);
        for (int i=0; i<msg.length; i++){
            BigInteger val = BigInteger.valueOf(msg[i]);
            BigInteger s = val.modPow(de, n);
            msg[i] = s.intValue();
        }
        return msg;
    }
}
