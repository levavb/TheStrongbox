package com.example.thestrongbox.Model;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class CryptoHash {


    public static byte[] getSha256( String pass ) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance( "SHA-256" );
        // Change this to UTF-16 if needed
        md.update( pass.getBytes( StandardCharsets.UTF_8 ) );

        byte[] digestTmp = md.digest();
        byte[] digest = new byte[16];
        System.arraycopy(digestTmp,0,digest,0,16);

        return md.digest();
    }

    public static byte[] emptySHA() {
        return "0000000000000000".getBytes();
    }

}
