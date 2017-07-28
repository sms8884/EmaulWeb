package com.jaha.web.emaul.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by imac1 on 2016. 3. 7..
 */
public class EmaulCrypto {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);

    public static String getTimeStampString() {
        return format.format(new Date());
    }

    static final int KEY_CHECK_BIT = 0xA1;
    static final int KEY_VERSION = 0x01;

    static final int RSA_INPUT_SIZE = 240;
    public static final int RSA_ENC_SIZE = 256;

    public class VoteKey {
        public String publicKey;
        public String privatKey;
    }

    class RsaKey {
        public String module;
        public String publicExponent;
        public String privateExponent;
    }

    public static byte[] encryptAES(byte[] data, byte[] key, byte[] iv) throws Exception {
        try {
            return process("AES", "AES/CBC/NoPadding", Cipher.ENCRYPT_MODE, data, key, iv);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static byte[] decryptAES(byte[] data, byte[] key, byte[] iv) throws Exception {
        try {
            return process("AES", "AES/CBC/NoPadding", Cipher.DECRYPT_MODE, data, key, iv);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static byte[] process(String crypt, String option, int mode, byte[] data, byte[] key, byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecretKeySpec keySpec = new SecretKeySpec(key, crypt);
        IvParameterSpec ivParam = new IvParameterSpec(iv);
        Cipher nCipher = Cipher.getInstance(option);
        nCipher.init(mode, keySpec, ivParam);

        return nCipher.doFinal(data);
    }

    public static byte[] encryptSHA256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(data);
        return messageDigest.digest();
    }

    public static byte[] encryptSHA256(String txt) throws NoSuchAlgorithmException {
        return encryptSHA256(txt.getBytes());
    }

    public static byte[] pdkdf2(String password, String salt, int iterations, int length) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, length);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(spec).getEncoded();
    }

    // algorithm
    final String VOTE_RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";


    private Cipher getCipherRSAInstance() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(VOTE_RSA_ALGORITHM);
    }


    public byte[] encryptRSA(String key, byte[] data) throws Exception {
        String[] pubKey = key.split(":");

        BigInteger modulus = new BigInteger(pubKey[0], 16);
        BigInteger exponent = new BigInteger(pubKey[1], 16);
        RSAPublicKeySpec pubks2 = new RSAPublicKeySpec(modulus, exponent);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey2 = keyFactory.generatePublic(pubks2);

        Cipher cipher = getCipherRSAInstance();
        int encSize = (int) Math.ceil((double) data.length / (double) RSA_INPUT_SIZE) * RSA_ENC_SIZE;

        ByteBuffer byteBuffer = ByteBuffer.allocate(encSize);
        int encLen = 0;
        for (int i = 0; i < data.length; i += RSA_INPUT_SIZE) {
            encLen = Math.min(data.length - i, RSA_INPUT_SIZE);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
            byteBuffer.put(cipher.doFinal(data, i, encLen));
        }
        return byteBuffer.array();
    }

    public byte[] decryptRSA(String key, byte[] data) throws Exception {
        String[] priKey = key.split(":");
        BigInteger modulus = new BigInteger(priKey[0], 16);
        BigInteger exponent = new BigInteger(priKey[1], 16);
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(privateKeySpec);

        Cipher cipher = getCipherRSAInstance();
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);

        for (int i = 0; i < data.length; i += RSA_ENC_SIZE) {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byteBuffer.put(cipher.doFinal(data, i, RSA_ENC_SIZE));
        }
        int pos = byteBuffer.position();
        byteBuffer.flip();

        byte[] ret = new byte[pos];
        byteBuffer.get(ret);

        return ret;
    }

    public static byte[] getRandomByte(int byteLen) {
        Random rand = new Random(System.currentTimeMillis());
        byte ret[] = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            ret[i] = (byte) (rand.nextInt(254) + 1);
        }
        return ret;
    }


    public static int toInteger(byte src) {
        return src & 0xff;
    }

    public static byte[] getByte(String data, String charsetName, int outLen) throws UnsupportedEncodingException {
        byte[] out = new byte[outLen];
        byte[] src = data.getBytes(charsetName);
        System.arraycopy(src, 0, out, 0, Math.min(src.length, out.length));
        return out;
    }

    public static byte[] getByte(String data, int outLen) {
        byte[] out = new byte[outLen];
        byte[] src = data.getBytes();
        System.arraycopy(src, 0, out, 0, Math.min(src.length, out.length));
        return out;
    }

    public static byte[] getByte(ByteBuffer data, int length) {
        byte[] dst = new byte[length];
        data.get(dst);
        return dst;
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }


    public RsaKey generateRsaKey() throws Exception {

        RsaKey rsaKey = new RsaKey();
        KeyPairGenerator keyPairGenerator = null;
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        for (int i = 0; i < 5; i++) {

            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec clsPublicKeySpec = fact.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec clsPrivateKeySpec = fact.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

            rsaKey.publicExponent = clsPublicKeySpec.getPublicExponent().toString(16);
            rsaKey.privateExponent = clsPrivateKeySpec.getPrivateExponent().toString(16);
            rsaKey.module = clsPublicKeySpec.getModulus().toString(16);

            if (rsaKey.privateExponent.length() == 512 && rsaKey.module.length() == 512) {
                return rsaKey;
            }

            System.out.println("generateRsaKey Erroor:" + i);
        }
        throw new Exception("generateRsaKey error..");
    }

    final int EMAUL_KEY_HEADER_LEN = 16;

    public class Header {
        public int startBit;
        public int ptlVer;
        public String mark;
        byte[] reserved = new byte[4];
    }

    public static String getString(ByteBuffer data, int length) {
        byte[] dst = new byte[length];
        data.get(dst);
        return new String(dst);
    }

    public static String getString(ByteBuffer data, int length, String charsetName) throws UnsupportedEncodingException {
        byte[] dst = new byte[length];
        data.get(dst);
        return new String(dst, charsetName);
    }


    public byte[] makeHeader(int version) {
        byte[] reserved = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(EMAUL_KEY_HEADER_LEN);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) KEY_CHECK_BIT);
        buffer.put((byte) KEY_VERSION);
        buffer.put(getByte("EMAUL", 10));
        buffer.put(reserved);
        return buffer.array();
    }

    public byte[] makeHeader() {
        return makeHeader(KEY_VERSION);
    }

    public Header getHeader(byte[] data) {
        Header header = new Header();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);
        header.startBit = toInteger(buffer.get());
        header.ptlVer = toInteger(buffer.get());
        header.mark = getString(buffer, 10);
        return header;
    }

    protected boolean checkHeader(Header header) {
        if (header.startBit == 0xA1) {
            return true;
        }

        return false;
    }

    public VoteKey generateVoteKey(String keyString, String password1, String password2, String password3) throws Exception {
        VoteKey voteKey = new VoteKey();
        byte[] pdkd = pdkdf2(password1 + password2 + password3, keyString, 100, 32 * 8);
        byte[] aesKey = new byte[16];
        byte[] aesIv = new byte[16];

        System.arraycopy(pdkd, 0, aesKey, 0, 16);
        System.arraycopy(pdkd, 16, aesIv, 0, 16);

        RsaKey rsaKey = generateRsaKey();

        String data = rsaKey.module + rsaKey.privateExponent;

        byte[] hash = encryptSHA256(data);
        byte[] encData = encryptAES(data.getBytes(), aesKey, aesIv);
        byte[] header = makeHeader();

        voteKey.privatKey = byteArrayToHex(header) + ":" + byteArrayToHex(encData) + ":" + byteArrayToHex(hash);
        voteKey.publicKey = rsaKey.module + ":" + rsaKey.publicExponent;

        // System.out.println("RSA privatKey:"+voteKey.privatKey);
        // System.out.println("RSA publicKey:"+voteKey.publicKey);
        return voteKey;
    }


    public static final String KEY_VOTE_ENC = "VOTE_ENC";
    public static final String KEY_VOTE_INS = "VOTE_INS";

    public String decryptVoteKey(String keyString, String password1, String password2, String password3, String encryptKey) throws VoteCryptoException {
        try {

            byte[] pdkd = pdkdf2(password1 + password2 + password3, keyString, 100, 32 * 8);
            byte[] aesKey = new byte[16];
            byte[] aesIv = new byte[16];

            System.arraycopy(pdkd, 0, aesKey, 0, 16);
            System.arraycopy(pdkd, 16, aesIv, 0, 16);

            String[] data = encryptKey.split(":");
            if (data.length != 3) {
                throw new VoteCryptoException("Data Error");
            }

            Header header = getHeader(hexToByteArray(data[0]));
            if (!checkHeader(header)) {
                throw new VoteCryptoException("Header Error");
            }
            String decData = new String(decryptAES(hexToByteArray(data[1]), aesKey, aesIv));
            String hash = byteArrayToHex(encryptSHA256(decData));


            decData = decData.substring(0, 512) + ":" + decData.substring(512);
            if (!hash.equals(data[2])) {
                throw new VoteCryptoException("Hash Error");
            }
            return decData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new VoteCryptoException("VoteKey", e);
        }
    }

    public static String encryptAdminPasswrod(String passwrd, String name) throws VoteCryptoException {
        System.out.println("passwd:" + passwrd);
        System.out.println("name :" + name);
        try {
            byte[] pdkd = pdkdf2(passwrd, name, 5000, 64 * 8);
            byte[] key = new byte[16];
            byte[] iv = new byte[16];
            byte[] data = new byte[32];

            System.arraycopy(pdkd, 0, key, 0, 16);
            System.arraycopy(pdkd, 16, iv, 0, 16);
            System.arraycopy(pdkd, 32, data, 0, 32);
            return byteArrayToHex(encryptAES(data, key, iv));
        } catch (Exception e) {
            throw new VoteCryptoException("Exception", e);
        }
    }

    /*
     * public static void main(String[] args) throws Exception {
     * 
     * 
     * EmaulCrypto crypto = new EmaulCrypto(); VoteKey encKey = crypto.generateVoteKey(KEY_VOTE_ENC, "12343434", "1232321321", "43432423423"); System.out.println("encKey.publicKey:" +
     * encKey.publicKey);
     * 
     * String edata = "1234789";
     * 
     * for(int i=0;i<1024*100;i++){ //edata+="1"; }
     * 
     * System.out.println("orgData size:" + edata.getBytes().length);
     * 
     * byte[] encData = crypto.encryptRSA(encKey.publicKey, edata.getBytes()); System.out.println("encData size:" + encData.length);
     * 
     * 
     * String priKey = crypto.decryptVoteKey(KEY_VOTE_ENC, "12343434", "1232321321", "43432423423", encKey.privatKey);
     * 
     * 
     * System.out.println("priKey:" + priKey);
     * 
     * System.out.println(getTimeStampString()); byte [] decData= crypto.decryptRSA(priKey, encData); System.out.println(getTimeStampString());
     * 
     * System.out.println("data:"+new String(decData));
     * 
     * }
     */
}
