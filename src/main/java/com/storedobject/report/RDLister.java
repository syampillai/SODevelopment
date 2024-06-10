package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFElement;
import com.storedobject.pdf.TableHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RDLister<T extends StoredObject> {

    private final ObjectIterator<T> objects;
    private final TableHeader tableHeader;

    public RDLister(ObjectLister<T> lister) {
        ReportDefinition reportDefinition = lister.getReportDefinition();
        List<ReportColumnDefinition> columns = reportDefinition.getColumns();
        String[] captions = new String[columns.size()];
        int i;
        for(i = 0; i < captions.length; i++) {
            captions[i] = lister.getColumnCaption(columns.get(i).getAttribute(), i);
        }
        tableHeader = new TableHeader(captions);
        ObjectIterator<T> objects = reportDefinition.listObjects(lister.getExtraCondition(), lister.getOrderBy());
        Predicate<T> filter = lister.getLoadFilter();
        if(filter != null) {
            objects = objects.filter(filter);
        }
        objects = lister.customizeList(objects);
        List<T> head = new ArrayList<>();
        int count = 20;
        while(count-- > 0) {
            if(objects.hasNext()) {
                head.add(objects.next());
            } else {
                break;
            }
        }
        this.objects = ObjectIterator.create(head).add(objects);
        if(head.isEmpty()) {
            return;
        }
        int[] w = new int[columns.size()];
        for(i = 0; i < w.length; i++) {
            w[i] = columns.get(i).getRelativeWidth();
            if(w[i] == 0) {
                w[i] = lister.getCharCount(captions[i]);
            }
        }
        ReportColumnDefinition c;
        int width;
        for(T so: head) {
            for(i = 0; i < columns.size(); i++) {
                c = columns.get(i);
                if(c.getRelativeWidth() == 0) {
                    width = lister.getCharCount(c.getValue().apply(so));
                    if(width > w[i]) {
                        w[i] = Math.min(width, 30);
                    }
                }
            }
        }
        tableHeader.setWidths(w);
        T so = head.get(0);
        for(i = 0; i < columns.size(); i++) {
            c = columns.get(i);
            if(c.getHorizontalAlignment() == 0) {
                c.setHorizontalAlignment(Utility.isRightAligned(c.getValue().apply(so)) ? 3 : 1);
            }
            tableHeader.setHorizontalAlignment(i, switch(c.getHorizontalAlignment()) {
                case 1 -> PDFElement.ALIGN_LEFT;
                case 2 -> PDFElement.ALIGN_CENTER;
                case 3 -> PDFElement.ALIGN_RIGHT;
                default -> PDFElement.ALIGN_UNDEFINED;
            });
            tableHeader.setVerticalAlignment(i, switch(c.getVerticalAlignment()) {
                case 0 -> PDFElement.ALIGN_TOP;
                case 1 -> PDFElement.ALIGN_CENTER;
                case 2 -> PDFElement.ALIGN_BOTTOM;
                default -> PDFElement.ALIGN_UNDEFINED;
            });
        }
        lister.customizeTableHeader(tableHeader);
    }

    public TableHeader getTableHeader() {
        return tableHeader;
    }

    public ObjectIterator<T> listObjects() {
        return objects;
    }
}
