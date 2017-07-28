// package com.jaha.web.emaul;
//
// import java.security.NoSuchAlgorithmException;
// import java.security.spec.InvalidKeySpecException;
//
// import com.jaha.web.emaul.util.PasswordHash;
// import com.softforum.xdbe.XdspNative;
//
// public class XecuredbCryptoTest {
//
// public static void main(String[] args) {
// String poolname = "pool1";
// String database = "jaha_db";
// String owner = "jaha_ow";
// String table = "jaha_tb";
// String column = "normal";
//
// try {
// String sHash = null;
// String sEncrypt = null;
// byte[] sDecrypt = null;
// String sString = "admin@jahasmart.com";
// XdspNative.setPropertiesFile("C:\\xecuredb\\conf\\xdsp_pool.properties");
// long nStartTime = System.currentTimeMillis();
// sEncrypt = XdspNative.fast_sync_encrypt64(poolname, database, owner, table, column, sString.getBytes());
//
// System.out.println("encrypt = " + sEncrypt);
//
// sDecrypt = XdspNative.fast_sync_decrypt64(poolname, database, owner, table, column, sEncrypt);
// sEncrypt = new String(sDecrypt);
// System.out.println("decrypt = " + sEncrypt);
// sHash = XdspNative.hash64(8, sString.getBytes());
// System.out.println("hash = " + sHash);
// long nEndTime = System.currentTimeMillis();
// long nDuration = nEndTime - nStartTime;
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// try {
// String newPasswd = PasswordHash.createHash("11111111");
// System.out.println("newPasswd = " + newPasswd);
// } catch (NoSuchAlgorithmException e) {
// e.printStackTrace();
// } catch (InvalidKeySpecException e) {
// e.printStackTrace();
// }
// }
// }
//
