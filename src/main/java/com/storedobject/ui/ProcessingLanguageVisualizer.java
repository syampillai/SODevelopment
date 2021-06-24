package com.storedobject.ui;

import com.storedobject.core.MediaFile;
import com.storedobject.core.ProcessingLanguage;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.ColumnSpan;
import com.storedobject.helper.ID;

public class ProcessingLanguageVisualizer extends IFrame implements ColumnSpan {

    public ProcessingLanguageVisualizer(String sourceFile) {
        this(ProcessingLanguage.get(sourceFile));
    }

    public ProcessingLanguageVisualizer(ProcessingLanguage source) {
        ID.set(this);
        setSourceDocument(html(source, getId().orElse("")), false);
        getElement().getStyle().set("overflow", "hidden");
    }

    private static String html(TextContent source, String id) {
        SOServlet.cacheTextContent(source);
        String cid = "so" + ID.newID();
        MediaFile pl = SOServlet.getMedia("ProcessingLanguage");
        StringBuilder s = new StringBuilder();
        s.append("<html><head><script src='");
        if(pl != null) {
            s.append("media/").append(pl.getFileName());
        }
        s.append("'></script><script>function sizer(){let c=document.getElementById('").append(cid);
        s.append("');let f=window.top.document.getElementById('");
        s.append(id).append("');let w=c.getAttribute('width')+'px';let h=c.getAttribute('height')+'px';f.setAttribute('width',w);f.setAttribute('height',h);");
        s.append("}</script></head><body style='margin:0px;' onload=\"{const e=document.getElementById('").append(cid);
        s.append("');const ro=new ResizeObserver(ev=>{sizer();});ro.observe(e);}\"><canvas id='").append(cid).append("' data-processing-sources='");
        if(source != null) {
            s.append("tc/").append(source.getId());
        }
        s.append("'></canvas></body></html>");
        return s.toString();
    }
}