import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.*;
 
public class RSA
{
    private BigInteger P;
    private BigInteger Q;
    private BigInteger N;
    private BigInteger PHI;
    private BigInteger e;
    private BigInteger d;
    private int maxLength = 1024;
    private Random R;

    private static HashMap<Character, Integer> map = new HashMap<>();
    public RSA()
    {
        R = new Random();
        P = BigInteger.probablePrime(maxLength, R);
        Q = BigInteger.probablePrime(maxLength, R);
        N = P.multiply(Q);
        PHI = P.subtract(BigInteger.ONE).multiply(  Q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(maxLength / 2, R);
        while (PHI.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(PHI) < 0)
        {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(PHI);
    }
 
    public static void main (String [] arguments) throws IOException
    {
        RSA rsa = new RSA();
        DataInputStream input = new DataInputStream(System.in);
        String inputString;
        System.out.println("\nEnter message you wish to send-");
        inputString = input.readLine();
        System.out.println("\nEncrypting the received message....... ");

        long start_entime = System.currentTimeMillis();
        // Pre-encryption 
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
        int enarr[] = new int[inputString.length()];

        // String to numbers
        for (int j=0; j<inputString.length(); j++){
            enarr[j] = map.get(inputString.charAt(j));
        }
        enarr = rsa.encryptMessage(enarr);
        long end_entime = System.currentTimeMillis();
        System.out.println("Finished Encrypting!");
        System.out.println("Encryption time: ");
        System.out.println(end_entime-start_entime+"ms");
        System.out.println("\nEncrypted Message/Cipher Text: ");
        for (int n: enarr) System.out.print(n+" ");


        // Decryption initiation
        System.out.println("\n\nDecrypting received cipher text....... ");
        long start_detime = System.currentTimeMillis();
        enarr = rsa.decryptMessage(enarr);
        StringBuffer sb = new StringBuffer();
        for (int k=0; k<enarr.length; k++){
            int val = enarr[k];
            for (char key: map.keySet()){
                if (map.get(key)==val){
                    sb.append(key);
                    break;
                }
            }
        }
        System.out.println("Finished Decrypting....... ");
        long end_detime = System.currentTimeMillis();
        System.out.println("Decryption time: ");
        System.out.println(end_detime-start_detime+"ms");
        System.out.println("\nDecrypted Message/Received Message-- "+sb.toString());
    }
 
    // Encrypting the message
    public int[] encryptMessage(int[] msg){
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
    public int[] decryptMessage(int[] msg){
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
