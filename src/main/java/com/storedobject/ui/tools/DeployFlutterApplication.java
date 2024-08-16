package com.storedobject.ui.tools;

import com.storedobject.common.IO;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.SQLConnector;
import com.storedobject.ui.ZipUploadProcessorView;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.View;

import java.io.*;
import java.util.zip.ZipEntry;

public class DeployFlutterApplication extends DataForm {

    private String folder;

    public DeployFlutterApplication() {
        super("Deploy Flutter Web Application");
        TextArea instruction = new TextArea("Flutter Web Application Package");
        String ins = """
                Do the following to package your flutter web application in your project folder:
                flutter build web --release --base-href=/flutter/"""
                + SQLConnector.getDatabaseName() + "/\n" +
                """
                cd build/web
                rm -fr ../../web.zip
                zip -r ../../web.zip *
                cd ../../

                Upload the web.zip file from your project folder.
                
                After uploading, you should be able to access your flutter application via the following link:
                """ + "\n" + SOServlet.getURL() + "/flutter/" + SQLConnector.getDatabaseName() + "/";
        instruction.setText(ins);
        addField(instruction);
        setFieldReadOnly(instruction);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Upload");
        ok.setIcon("upload");
    }

    @Override
    public int getMinimumContentWidth() {
        return 40;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        folder = ApplicationServer.getGlobalProperty("application.flutter.path", "/home/tomcat/flutter/");
        if(!folder.endsWith(File.separator)) {
            folder += File.separator;
        }
        folder += SQLConnector.getDatabaseName() + File.separator;
        File file = new File(folder);
        if(!file.exists() || !file.isDirectory() || !file.canWrite()) {
            StringBuilder s = new StringBuilder("Check flutter folder: ");
            s.append(file.getAbsolutePath());
            if(!file.exists()) {
                s.append(" - Not exists");
            }
            if (!file.isDirectory()) {
                s.append(" - Not a directory");
            }
            if(!file.canWrite()) {
                s.append(" - Not writable");
            }
            log(s);
            warning("Flutter application support not configured");
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        close();
        new Deploy().execute();
        return true;
    }

    private class Deploy extends ZipUploadProcessorView {

        public Deploy() {
            super(DeployFlutterApplication.this.getCaption());
            getUploadComponent().setMaxFileSize(104857600);
        }

        @Override
        protected void process(ZipEntry zipEntry, InputStream content) throws IOException {
            File file = new File(folder + zipEntry.getName());
            if(zipEntry.isDirectory()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdir();
            } else {
                OutputStream out = new FileOutputStream(file);
                IO.copy(content, out, false);
                out.close();
            }
        }
    }
}
