package com.storedobject.ui.tools;

import com.storedobject.common.IO;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.PasswordField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;
import com.storedobject.vaadin.TextField;

import java.io.InputStream;
import java.text.SimpleDateFormat;

public class DataDownload extends DataForm implements Transactional {

    private final PasswordField adminPassword;
    private final RadioChoiceField format = new RadioChoiceField("Format", new String[] { "Compressed", "Plain" });
    private final ELabelField plainWarn = new ELabelField(null,
            "File size will be much larger for plain format!", Application.COLOR_ERROR);
    private final TextField databaseName = new TextField("Name of the database");

    public DataDownload() {
        super("Data Download");
        ELabelField warn = new ELabelField("Warning");
        warn.append("Data will be downloaded to your machine ", Application.COLOR_SUCCESS);
        warn.newLine();
        warn.append("and you have to make sure that your machine has enough free space available",
                Application.COLOR_ERROR).update();
        addField("Warning", warn);
        adminPassword = new PasswordField("Administrator Password");
        adminPassword.setMaxLength(30);
        addField(adminPassword);
        addField(plainWarn);
        addField(format);
        plainWarn.setVisible(false);
        format.addValueChangeListener(e -> plainWarn.setVisible(e.getValue() == 1));
        addField(databaseName);
        databaseName.setValue(SQLConnector.getDatabaseName());
        SystemUser su = getTransactionManager().getUser();
        if(!(su.isAdmin() || su.isAppAdmin())) {
            setFieldReadOnly(databaseName);
        }
        setRequired(databaseName);
    }

    @Override
    public int getMaximumContentWidth() {
        return 50;
    }

    @Override
    protected boolean process() {
        clearAlerts();
        char[] password = adminPassword.getValue().toCharArray();
        try {
            if(!Database.get().validateSecurityPassword(password)) {
                throw new SOException("Administrator password is invalid");
            }
        } catch(Exception e) {
            warning(e);
            return false;
        }
        String dbName = databaseName.getValue();
        if(!Database.get().databaseExists(dbName)) {
            warning("Database doesn't exist: " + dbName);
            return false;
        }
        boolean sql = format.getValue() == 1;
        Process dump = RawSQL.dumpDatabase(password, sql, dbName);
        if(dump == null) {
            error("Technical error, please contact Technical Support");
            return false;
        }
        Application.get().download(new Data(dump.getInputStream(), sql, dbName));
        return true;
    }

    private static class Data extends StreamContentProducer {

        private final InputStream data;
        private final boolean sql;
        private final String dbName;

        private Data(InputStream data, boolean sql, String dbName) {
            this.data = data;
            this.sql = sql;
            this.dbName = dbName;
        }

        @Override
        public void generateContent() throws Exception {
            IO.copy(data, out);
        }

        @Override
        public String getFileName() {
            return dbName + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(DateUtility.now());
        }

        @Override
        public String getFileExtension() {
            return sql ? "sql" : "db";
        }

        @Override
        public String getContentType() {
            return sql ? "text/plain" : "application/bin";
        }
    }
}
