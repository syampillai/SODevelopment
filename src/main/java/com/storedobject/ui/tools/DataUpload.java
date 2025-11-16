package com.storedobject.ui.tools;

import com.storedobject.common.IO;
import com.storedobject.core.Database;
import com.storedobject.core.RawSQL;
import com.storedobject.core.SOException;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.PasswordField;
import com.storedobject.ui.UploadProcessorView;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataUpload extends DataForm {

    private final PasswordField adminPassword;
    private final TextField dbName;

    public DataUpload() {
        super("Data Upload");
        adminPassword = new PasswordField("Administrator Password");
        adminPassword.setMaxLength(30);
        dbName = new TextField("Database Name");
        addField(adminPassword, dbName);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        char[] password = adminPassword.getValue().toCharArray();
        try {
            if(!Database.get().validateSecurityPassword(password)) {
                throw new SOException("Administrator password is invalid!");
            }
        } catch(Exception e) {
            warning(e);
            return false;
        }
        String databaseName = dbName.getValue();
        Process restore = RawSQL.restoreDatabase(password, databaseName);
        if(restore == null) {
            error("Technical error, please contact Technical Support");
            return false;
        }
        close();
        new Upload(restore, databaseName).execute();
        return true;
    }

    private static class Upload extends UploadProcessorView {

        private final Process restore;

        public Upload(Process restore, String databaseName) {
            super("Upload Database: " + databaseName);
            this.restore = restore;
            add(new ELabel("Choose the database file to upload...", Application.COLOR_SUCCESS));
            getUploadComponent().setMaxFileSize(Integer.MAX_VALUE);
        }

        @Override
        protected void process(InputStream content, String mimeType) {
            if(mimeType != null && mimeType.equals("application/zip")) {
                processZip(content);
                return;
            }
            if(mimeType != null && mimeType.equals("application/gzip")) {
                processGZip(content);
                return;
            }
            if(mimeType != null && !mimeType.equals("application/sql")) {
                redMessage("Invalid file type, only SQL files are supported - " + mimeType);
                super.process(content, mimeType);
                return;
            }
            Thread.startVirtualThread(this::output);
            Thread.startVirtualThread(this::error);
            try {
                IO.copy(IO.getReader(content), restore.outputWriter());
                restore.waitFor();
                restore.destroy();
                blueMessage("Data uploaded successfully!");
            } catch(Exception e) {
                redMessage(e);
            }
        }

        private void output() {
            BufferedReader result = restore.inputReader();
            String line;
            int count = 0;
            while(true) {
                try {
                    if((line = result.readLine()) == null) {
                        break;
                    }
                } catch(IOException e) {
                    break;
                }
                if(++count == 500) {
                    redMessage("Output trimmed!!");
                    continue;
                }
                if(count > 500) {
                    continue;
                }
                blackMessage(line);
            }
            IO.close(result);
        }

        private void error() {
            BufferedReader result = restore.errorReader();
            String line;
            while(true) {
                try {
                    if((line = result.readLine()) == null) {
                        break;
                    }
                } catch(IOException e) {
                    break;
                }
                redMessage(line);
            }
            IO.close(result);
        }

        private void processZip(InputStream content) {
            ZipInputStream zin = new ZipInputStream(content);
            try {
                ZipEntry zipEntry;
                boolean ignore = false;
                while(true) {
                    try {
                        zipEntry = zin.getNextEntry();
                        if(zipEntry == null) {
                            break;
                        }
                        if(ignore) {
                            redMessage("Ignoring zip content - " + zipEntry.getName());
                        } else {
                            ignore = true;
                            blueMessage("Processing zip content - " + zipEntry.getName());
                            process(zin, null);
                        }
                    } catch (IOException e) {
                        error(e);
                    }
                }
            } finally {
                IO.close(zin);
            }
        }

        private void processGZip(InputStream content) {
            blueMessage("Found GNU zipped stream.");
            try {
                GZIPInputStream gzip = new GZIPInputStream(content);
                blueMessage("GNU zipped stream decompressed.");
                process(gzip, null);
            } catch(IOException e) {
                error(e);
            }
        }
    }
}
