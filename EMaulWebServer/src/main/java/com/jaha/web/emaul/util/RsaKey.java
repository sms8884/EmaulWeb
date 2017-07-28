/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 7. 14.
 */
package com.jaha.web.emaul.util;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase.powerbuilder.cryptography.PBCrypto;

/**
 * <pre>
 * Class Name : RsaKey.java
 * Description : Description
 *  
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 7. 14.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 7. 14.
 * @version 1.0
 */
public class RsaKey {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** 암호화 알고리즘 명 */
    public static final String ALGO_NAME = "RSA";
    /** 암호화 할 수 있는 비트수, 키사이즈가 클수록 보다 많이 암호화가 가능하다. 512, 1024, 2048 */
    public static final int KEY_LENGTH = 1024;

    public static final String PUBLIC_KEY_EXPONENT = "10001";

    private String publicKey;

    private String privateKey;

    /**
     * @return the publicKey
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * @return the privateKey
     */
    public String getPrivateKey() {
        return privateKey;
    }

    public RsaKey() {
        this.generateRsaKey();
    }

    private void generateRsaKey() {
        PBCrypto pbc = new PBCrypto();

        try {
            String[] rsaKeys = pbc.createRSAKeyPair();
            // System.out.println("Public key : \n" + rsaKeys[0]);
            // System.out.println("Private Key : \n" + rsaKeys[1]);

            this.publicKey = rsaKeys[0];
            this.privateKey = rsaKeys[1];
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        }
    }

    // public static void main(String[] args) {
    // RsaKey rk = new RsaKey();
    // System.out.println(rk.getPublicKey());
    // System.out.println();
    // System.out.println(rk.getPrivateKey());
    // }

}
