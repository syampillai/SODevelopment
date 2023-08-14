package com.storedobject.ui.tools;

import com.storedobject.common.IO;
import com.storedobject.core.License;
import com.storedobject.core.TextContentProducer;
import com.storedobject.core.TransactionManager;
import com.storedobject.ui.Application;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.UploadProcessorView;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

import java.io.BufferedReader;
import java.io.InputStream;

public class LicenseManagement extends DataForm implements Transactional {

    private final ChoiceField choice = new ChoiceField("Choose", new String[] {
            "Generate request for new license on this server",
            "Generate request for updated license for this server",
            "Delete the current license on this server",
            "Transfer the license to another server",
            "Upload a license"
    });

    public LicenseManagement() {
        super("License Management");
        addField(choice);
    }

    @Override
    protected boolean process() {
        int c = choice.getValue();
        if(c == 4) {
            close();
            new ProcessLicense().execute();
            return true;
        }
        try {
            TransactionManager tm = getTransactionManager();
            String lic = switch(c) {
                case 0 -> License.requestForNew(tm);
                case 1 -> License.requestForUpdate(tm);
                case 2 -> License.requestForDeletion(tm);
                case 3 -> License.transfer(tm, null);
                default -> "";
            };
            TextContentProducer request = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    getWriter().write(lic);
                }

                @Override
                public String getFileName() {
                    return "LicenseRequest";
                }
            };
            Application.get().download(request);
            warning("The license request is generated and downloaded");
            close();
            return true;
        } catch(Throwable e) {
            error(e);
        }
        return false;
    }

    @Override
    public int getMinimumContentWidth() {
        return 40;
    }

    private static class ProcessLicense extends UploadProcessorView {

        public ProcessLicense() {
            super("Upload License");
        }

        @Override
        protected void process(InputStream content, String mimeType) {
            try(BufferedReader br = IO.getReader(content)) {
                License.process(getTransactionManager(), br.readLine());
                blueMessage("License successfully uploaded");
            } catch(Throwable e) {
                redMessage(e);
            }
        }
    }
}
