package com.storedobject.ui.tools;

import com.storedobject.common.IO;
import com.storedobject.common.StringList;
import com.storedobject.common.SystemProcess;
import com.storedobject.common.XML;
import com.storedobject.core.*;
import com.storedobject.ui.DataGrid;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;

public class SystemLogViewer extends View implements Transactional, CloseableView {

    private final IntegerField lines = new IntegerField("Lines", 0, 4);
    private final ObjectField<SystemUser> loginField = new ObjectField<>("User", SystemUser.class);
    private final TextArea logDump = new TextArea("Log");
    private final Button view = new Button("View", this);
    private final ChoiceField type = new ChoiceField("Type", new String[] { "User Logs", "Server Logs", "System Logs" });
    private final BooleanField includeGenericInfo = new BooleanField("Include Generic Information");
    private final TextField systemLogName = new TextField("System Log Name");
    private final SystemProcess process = new SystemProcess();
    private final LogGrid logGrid = new LogGrid();
    private final SystemLogGrid systemLogGrid = new SystemLogGrid();
    private final TimestampPeriodField systemLogPeriod = new TimestampPeriodField("System Log Period");
    private String login, tag;

    public SystemLogViewer() {
        super("System Log");
        systemLogName.uppercase();
        FormLayout form = new FormLayout(type, lines, new ButtonLayout(view), loginField, includeGenericInfo,
                systemLogName, systemLogPeriod, new ButtonLayout(new Button("Exit", e -> close())),
                logGrid, logDump, systemLogGrid);
        systemLogName.setVisible(false);
        systemLogPeriod.setValue(new TimestampPeriod(DateUtility.startOfToday(), getTransactionManager().date(DateUtility.now())));
        systemLogPeriod.setVisible(false);
        trackValueChange(type);
        trackValueChange(includeGenericInfo);
        form.setColumns(3);
        lines.setValue(50);
        form.setColumnSpan(logGrid, form.getColumns());
        form.setColumnSpan(logDump, form.getColumns());
        form.setColumnSpan(systemLogGrid, form.getColumns());
        setComponent(form);
        hideLogs();
    }

    private void hideLogs() {
        logGrid.setVisible(false);
        logDump.setVisible(false);
        systemLogGrid.setVisible(false);
    }

    @Override
    public void clicked(Component c) {
        if(type.getValue() == 0 && loginField.getValue() == null) {
            loginField.setValue(getTransactionManager().getUser());
        }
        login = null;
        tag = null;
        switch(type.getValue()) {
            case 0 -> login = loginField.getObject().getLogin();
            case 1, 2 -> login = "*";
        }
        if(login == null) {
            SystemUser su = SystemUser.get(ApplicationServer.getGlobalProperty(tag + ".user", "*"));
            if(su == null) {
                warning("Can not identify user for the " + tag + " interface");
                hideLogs();
                return;
            }
            login = su.getLogin();
        }
        if(lines.getValue() <= 0) {
            lines.setValue(50);
        }
        if(c == view) {
            switch (type.getValue()) {
                case 0 -> {
                    logDump.setVisible(false);
                    systemLogGrid.setVisible(false);
                    loadUserLog();
                    logGrid.setVisible(true);
                }
                case 1 -> {
                    logGrid.setVisible(false);
                    systemLogGrid.setVisible(false);
                    loadServerLog();
                    logDump.setVisible(true);
                }
                case 2 -> {
                    logDump.setVisible(false);
                    logGrid.setVisible(false);
                    loadSystemLog();
                    systemLogGrid.setVisible(true);
                }
            }
        }
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.getChanged() == includeGenericInfo) {
            clicked(view);
            return;
        }
        if(changedValues.getChanged() == type) {
            loginField.setVisible(type.getValue() == 0);
            includeGenericInfo.setVisible(type.getValue() == 0);
            systemLogName.setVisible(type.getValue() == 2);
            systemLogPeriod.setVisible(type.getValue() == 2);
            clicked(view);
        }
    }

    private void loadServerLog() {
        try {
            process.setCommand("/usr/bin/tail");
            process.addCommand("-" + lines.getValue());
            process.addCommand(ApplicationServer.getGlobalProperty("application.log",
                    "/home/" + System.getProperty("user.name") + "/soengine/hosts/soengine.log"));
            process.execute();
            String error = process.getError();
            if(process.getExitValue() != 0) {
                warning(error);
                hideLogs();
            }
        } catch (Exception e) {
            error(e);
            hideLogs();
            return;
        }
        logDump.setValue(process.getOutput());
    }

    private void loadUserLog() {
        XML xml = new XML();
        xml.ignoreDTDs();
        try {
            xml.set(new TagClosedReader(tag));
            logGrid.setXML(xml);
        } catch(SOException soe) {
            String error = "Log File" + (tag == null ? "" : (" for " + Character.toUpperCase(tag.charAt(0))
                    + tag.substring(1))) + ": ";
            warning(error + soe.getEndUserMessage());
            log(error + TagClosedReader.fileName(tag));
            hideLogs();
        } catch (Exception e) {
            error(e);
            hideLogs();
        }
    }

    private void loadSystemLog() {
        String name = systemLogName.getValue();
        if(name.isBlank()) {
            systemLogName.focus();
            return;
        }
        TimestampPeriod period = systemLogPeriod.getValue();
        if(!period.isValid()) {
            systemLogPeriod.focus();
            return;
        }
        systemLogGrid.load(SystemLog.list(name, getTransactionManager().periodGMT(period), lines.getValue()));
    }

    private static class TagClosedReader extends Reader {

        private BufferedReader logReader;
        private final char[] tag = new char[] { '<', '/', 'l', 'o', 'g', '>' };
        private int tagRead = 0;
        private long fileTime;
        private int fileCount = 0;
        private final String fileName;

        private TagClosedReader(String tag) throws Exception {
            fileName = fileName(tag);
            File file = new File(fileName);
            if(!file.exists()) {
                throw new SOException("Not found!");
            }
            if(!file.canRead()) {
                throw new SOException("No permission!");
            }
            fileTime = file.lastModified();
            logReader = IO.getReader(file);
        }

        private static String fileName(String tag) {
            String fileName = ApplicationServer.getLogFile();
            if(tag != null) {
                int p = fileName.lastIndexOf(File.separatorChar);
                fileName = fileName.substring(0, p) + "-" + tag + fileName.substring(p);
            }
            return fileName;
        }

        @Override
        public int read(@SuppressWarnings("NullableProblems") char[] cbuf, int off, int len) throws IOException {
            int offset = off;
            int r;
            if(logReader != null) {
                r = logReader.read(cbuf, off, len);
                if(r == -1) {
                    logReader.close();
                    logReader = null;
                    ++fileCount;
                    File file = new File(fileName + "." + fileCount);
                    if(file.exists() && file.lastModified() > fileTime) {
                        fileTime = file.lastModified();
                        logReader = IO.getReader(file);
                        logReader.readLine();
                        logReader.readLine();
                        logReader.readLine();
                        return read(cbuf, off, len);
                    }
                } else {
                    sanitize(cbuf, offset, r);
                    return r;
                }
            }
            if(tagRead >= tag.length) {
                return -1;
            }
            r = 0;
            while(tagRead < tag.length && len > 0) {
                cbuf[off] = tag[tagRead];
                ++off;
                --len;
                ++tagRead;
                ++r;
            }
            sanitize(cbuf, offset, r);
            return r;
        }

        private void sanitize(char[] buf, int off, int count) {
            char c;
            while(count > 0 && off < buf.length) {
                --count;
                c = buf[off];
                if(c == 0x9 || c == 0xA || c == 0xD || c >= 0x20 && c <= 0xD7FF || c >= 0xE000 && c <= 0xFFFD) {
                    ++off;
                    continue;
                }
                buf[off] = '?';
                ++off;
            }
        }

        @Override
        public void close() throws IOException {
            if(logReader == null) {
                return;
            }
            logReader.close();
            tagRead = tag.length;
        }
    }

    private class LogGrid extends XMLGrid {

        private String user, app;
        private boolean all;

        public LogGrid() {
            super("/log/record", StringList.create("message", "level", "millis"));
            setAllRowsVisible(true);
            setDetailsVisibleOnClick(true);
            setItemDetailsRenderer(new ComponentRenderer<>(data -> new ELabel(restOf(data.getDataValue("message").toString()))));
        }

        @Override
        public String getFixedColumnWidth(String columnName) {
            return switch (columnName) {
                case "level" -> "80px";
                case "millis" -> "180px";
                default -> "200px";
            };
        }

        @Override
        public void customizeColumn(String columnName, Column<XMLNodeData> column) {
            if("message".equals(columnName)) {
                column.setFlexGrow(1);
            }
        }

        @Override
        public void setXML(XML xml) {
            user = "/" + login + "/";
            app = "(" + ApplicationServer.getPackageId();
            if(tag != null) {
                app += "-" + tag.toUpperCase();
            }
            app += ")/";
            all = SystemLogViewer.this.includeGenericInfo.getValue();
            super.setXML(xml);
        }

        @Override
        public void acceptNodeData(ArrayList<XMLNodeData> nodeData) {
            int count = lines.getValue();
            while (nodeData.size() > count) {
                nodeData.removeFirst();
            }
            if(nodeData.size() > 450) {
                int ps = (nodeData.size() + 50) / 10;
                if(getPageSize() < ps) {
                    setPageSize(ps);
                }
            }
        }

        @Override
        public boolean acceptNodeData(XMLNodeData xmlNodeData) {
            String m = (String) xmlNodeData.getDataValue("message");
            if(!m.startsWith(app)) {
                return all;
            }
            m = m.substring(app.length() - 1);
            return m.startsWith(user) || (all && m.startsWith("//"));
        }

        @Override
        public Object convertValue(Object value, XMLNodeData item, String columnName) {
            return switch(columnName) {
                case "millis" -> getTransactionManager().date(new Timestamp(Long.parseLong(value.toString())));
                case "message" -> firstLine(value.toString());
                case "level" -> StringUtility.makeLabel(value.toString().toLowerCase());
                default -> super.convertValue(value, item, columnName);
            };
        }

        private String sanitize(String m) {
            if(!m.startsWith(app)) {
                return m;
            }
            if(!m.contains("\n")) {
                return m;
            }
            m = m.substring(m.indexOf('\n') + 1);
            return m;
        }

        private String firstLine(String m) {
            m = sanitize(m);
            int p = m.indexOf('\n');
            return p < 0 ? m : m.substring(0, p);
        }

        private String restOf(String m) {
            m = sanitize(m);
            int p = m.indexOf('\n');
            return p < 0 ? "" : m.substring(p + 1);
        }

        @Override
        public String getColumnCaption(String columnName) {
            if(columnName.equals("millis")) {
                return "Time";
            }
            return super.getColumnCaption(columnName);
        }
    }

    @SuppressWarnings("unused")
    private static class SystemLogGrid extends DataGrid<SystemLog> {

        public SystemLogGrid() {
            super(SystemLog.class, StringList.create("Message", "LoggedAt"));
            setAllRowsVisible(true);
            setDetailsVisibleOnClick(true);
            setItemDetailsRenderer(new ComponentRenderer<>(systemLog -> new ELabel(systemLog.getMessage())));
        }

        @Override
        public int getRelativeColumnWidth(String columnName) {
            return columnName.equals("LoggedAt") ? 15 : 80;
        }

        public String getColumnCaption(String columnName) {
            return columnName.equals("LoggedAt") ? ("Time (" + getTransactionManager().getEntity().getTimeZone() + ")")
                    : super.getColumnCaption(columnName);
        }

        public String getLoggedAt(SystemLog systemLog) {
            return DateUtility.formatWithTime(getTransactionManager().date(systemLog.getLoggedAt()));
        }

        public String getMessage(SystemLog systemLog) {
            String m = systemLog.getMessage();
            int p = m.indexOf('\n');
            return p < 0 ? m : m.substring(0, p);
        }
    }
}
