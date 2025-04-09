package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.Data;
import com.storedobject.office.ExcelReport;
import com.storedobject.ui.Application;

import java.sql.Timestamp;
import java.util.function.Predicate;

public class DataDownload extends ExcelReport {

    private final Data4Unit data4Unit;
    private final int timeDifference;

    public DataDownload(Data4Unit data4Unit) {
        super(Application.get());
        this.data4Unit = data4Unit;
        timeDifference = data4Unit.unit().getSite().getTimeDifference();
    }

    @Override
    public void generateContent() throws Exception {
        StringList a = StringList.create(data4Unit.attributes());
        StoredObjectUtility.MethodList[] mls = StoredObjectUtility.createMethodLists(data4Unit.dataClass(), a);
        int row = 0;
        goToCell(0, row++);
        setCellValue("Site: " + data4Unit.unit().getSite().getName());
        goToCell(0, row++);
        setCellValue("Unit: " + data4Unit.unit().getName());
        goToCell(0, row++);
        for(String h: a) {
            setCellValue(h.substring(h.indexOf(" AS ") + 4));
            moveRight();
        }
        Predicate<Data> filter = data4Unit.sliceFilter();
        for(StoredObject so: StoredObject.list(data4Unit.dataClass(), data4Unit.condition(), "CollectedAt")
                .filter(filter)) {
            goToCell(0, row++);
            setCellValue(new Timestamp((long)mls[0].invoke(so, true) + timeDifference));
            getCell().setCellStyle(getDateTimeStyle());
            Object v;
            for(int i = 1; i < mls.length; i++) {
                moveRight();
                v = mls[i].invoke(so, true);
                if(v instanceof Boolean b) {
                    setCellValue(b ? 1 : 0);
                } else {
                    setCellValue(v);
                }
            }
        }
    }

    @Override
    public String getFileName() {
        return StringUtility.makeLabel(data4Unit.dataClass()) + " - "
                + DateUtility.formatWithTimeHHMM(new Timestamp(data4Unit.period().getFrom().getTime() + timeDifference))
                + " to "
                + DateUtility.formatWithTimeHHMM(new Timestamp(data4Unit.period().getTo().getTime() + timeDifference));
    }
}
