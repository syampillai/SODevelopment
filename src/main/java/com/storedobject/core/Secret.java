package com.storedobject.core;

import com.storedobject.common.HTTP;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Cipher keys used for encryption/decryption.
 *
 * @author Syam
 */
public final class Secret extends StoredObject {

    public Secret() {
    }

    public static void columns(Columns columns) {
    }

    public void setKeyIndex(int keyIndex) {
    }

    public int getKeyIndex() {
        return 0;
    }

    public void setSeed(String seed) {
    }

    public String getSeed() {
        return "";
    }

    public void setSeedChecker(String seed) {
    }

    public String getSeedChecker() {
        return "";
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public static String[] getTypeValues() {
        return new String[] { };
    }

    public static String getTypeValue(int type) {
        return "";
    }

    public String getTypeValue() {
        return "";
    }

    public void setKeySerial(int keySerial) {
    }

    public int getKeySerial() {
        return 0;
    }

    public void setComment(String comment) {
    }

    public String getComment() {
        return "";
    }


    /**
     * Encrypt data.
     *
     * @param data Data to encrypt.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @return Encrypted text.
     */
    public static String encrypt(String data, int keyIndex, TransactionManager tm) {
        return data;
    }

    /**
     * Encrypt data.
     *
     * @param data Data to encrypt.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @return Encrypted text.
     */
    public static String encrypt(String data, int keyIndex, SystemUser user) {
        return data;
    }

    /**
     * Decrypt data.
     *
     * @param data Data to decrypt.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @return Decrypted text.
     */
    public static String decrypt(String data, int keyIndex, TransactionManager tm) {
        return data;
    }

    /**
     * Decrypt data.
     *
     * @param data Data to decrypt.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @return Decrypted text.
     */
    public static String decrypt(String data, int keyIndex, SystemUser user) {
        return data;
    }

    /**
     * Decrypt the encrypted stream.
     *
     * @param encryptedStream Stream to decrypt.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @return Decrypted stream.
     */
    public static InputStream decrypt(InputStream encryptedStream, int keyIndex, TransactionManager tm)
            throws IOException {
        return encryptedStream;
    }

    /**
     * Decrypt the encrypted stream.
     *
     * @param encryptedStream Stream to decrypt.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @return Decrypted stream.
     */
    public static InputStream decrypt(InputStream encryptedStream, int keyIndex, SystemUser user)
            throws IOException {
        return encryptedStream;
    }

    /**
     * Encrypt the encrypted stream.
     *
     * @param clearStream Stream to encrypt.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @return Encrypted stream.
     */
    public static OutputStream encrypt(OutputStream clearStream, int keyIndex, TransactionManager tm)
            throws IOException {
        return clearStream;
    }

    /**
     * Encrypt the encrypted stream.
     *
     * @param clearStream Stream to encrypt.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @return Encrypted stream.
     */
    public static OutputStream encrypt(OutputStream clearStream, int keyIndex, SystemUser user)
            throws IOException {
        return clearStream;
    }

    /**
     * Encrypt a clear stream and write it to an output stream.
     *
     * @param clearStream Clear stream to read from.
     * @param encryptedStream Encrypted output stream.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @throws IOException If any IO exception occurs.
     */
    public static void encrypt(InputStream clearStream, OutputStream encryptedStream, int keyIndex,
                               TransactionManager tm) throws IOException {
    }

    /**
     * Encrypt a clear stream and write it to an output stream.
     *
     * @param clearStream Clear stream to read from.
     * @param encryptedStream Encrypted output stream.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @throws IOException If any IO exception occurs.
     */
    public static void encrypt(InputStream clearStream, OutputStream encryptedStream, int keyIndex,
                               SystemUser user) throws IOException {
    }

    /**
     * Decrypt an encrypted stream and write it to an output stream.
     *
     * @param encryptedStream Encrypted stream to read from.
     * @param clearStream Clear output stream.
     * @param keyIndex Index of the key to be used.
     * @param tm Transaction manager.
     * @throws IOException If any IO exception occurs.
     */
    public static void decrypt(InputStream encryptedStream, OutputStream clearStream, int keyIndex,
                               TransactionManager tm) throws IOException {
    }

    /**
     * Decrypt an encrypted stream and write it to an output stream.
     *
     * @param encryptedStream Encrypted stream to read from.
     * @param clearStream Clear output stream.
     * @param keyIndex Index of the key to be used.
     * @param user Verified system user
     * @throws IOException If any IO exception occurs.
     */
    public static void decrypt(InputStream encryptedStream, OutputStream clearStream, int keyIndex,
                               SystemUser user) throws IOException {
    }

    /**
     * Migrate Master Keys.
     *
     * @param tm Transaction manager.
     * @throws Exception If any error occurs.
     */
    public static void migrateMasterKey(TransactionManager tm) throws Exception {
    }

    /**
     * Definition of the Master Key supplier. The key should be an AES key.
     *
     * @author Syam
     */
    public interface MasterKeyProvider {

        /**
         * Get the active key that is currently in use.
         *
         * @return Currently active key.
         */
        SecretKey getCurrentKey();

        /**
         * Get the key that was previously active.
         *
         * @return Previously active key.
         */
        SecretKey getPreviousKey();
    }

    public static String obfuscate(String value) {
        return value + new Random().nextInt();
    }
    
    public static void authenticate(HTTP http, String user, String password) {
    }
}
