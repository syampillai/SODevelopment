package com.storedobject.iot;

import com.storedobject.core.*;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Definition of MQTT connector.
 *
 * @author Syam
 */
public class MQTT extends StoredObject {

    public MQTT() {
    }

    public static void columns(Columns columns) {
    }

    public static MQTT get(String name) {
        return StoredObjectUtility.get(MQTT.class, "Name", name, false);
    }

    public static ObjectIterator<MQTT> list(String name) {
        return StoredObjectUtility.list(MQTT.class, "Name", name, false);
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setServer(String server) {
    }

    public String getServer() {
        return "";
    }

    public void setPort(int port) {
    }

    public int getPort() {
        return 1;
    }

    public void setConnectionType(int connectionType) {
    }

    public int getConnectionType() {
        return 2;
    }

    public static String[] getConnectionTypeValues() {
        return new String[] {};
    }

    public static String getConnectionTypeValue(int value) {
        return "";
    }

    public String getConnectionTypeValue() {
        return "";
    }

    public void setKeyStoreFile(Id keyStoreFileId) {
    }

    public void setKeyStoreFile(BigDecimal idValue) {
    }

    public void setKeyStoreFile(StreamData keyStoreFile) {
        setKeyStoreFile(keyStoreFile == null ? null : keyStoreFile.getId());
    }

    public Id getKeyStoreFileId() {
        return new Id();
    }

    public StreamData getKeyStoreFile() {
        return getRelated(StreamData.class, new Id());
    }

    public void setKeyStorePassword(String keyStorePassword) {
    }

    public String getKeyStorePassword() {
        return "";
    }

    public void setUserName(String userName) {
    }

    public String getUserName() {
        return "";
    }

    public void setUserPassword(String userPassword) {
    }

    public String getUserPassword() {
        return "";
    }

    public void setConnectionTimeout(int connectionTimeout) {
    }

    public int getConnectionTimeout() {
        return 1;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
    }

    public int getKeepAliveInterval() {
        return 1;
    }

    public void setMQTTVersion(int mQTTVersion) {
    }

    public int getMQTTVersion() {
        return 0;
    }

    public static String[] getMQTTVersionValues() {
        return new String[] {};
    }

    public static String getMQTTVersionValue(int value) {
        return "";
    }

    public String getMQTTVersionValue() {
        return "";
    }

    public void setTopic(String topic) {
    }

    public String getTopic() {
        return "";
    }

    public void setModuleName(String moduleName) {
    }

    public String getModuleName() {
        return "";
    }

    public void setTimeFormat(int timeFormat) {
    }

    public int getTimeFormat() {
        return 0;
    }

    public static String[] getTimeFormatValues() {
        return new String[] {};
    }

    public static String getTimeFormatValue(int value) {
        return "";
    }

    public String getTimeFormatValue() {
        return "";
    }

    public Timestamp parseTime(String time) {
        return new Timestamp(0);
    }

    public void addListener(IMqttMessageListener listener) {
    }

    public void removeListener(IMqttMessageListener listener) {
    }

    public void removeAllListeners() {
    }

    public void connect() throws Exception {
    }

    public String dumpConfiguration() throws Exception {
        return "";
    }

    public void disconnect() {
    }

    public void collect(TransactionManager tm) throws Exception {
    }
}
