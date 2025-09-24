package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.Data;
import com.storedobject.office.CSVReport;
import com.storedobject.ui.Application;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class DataDownload extends CSVReport {

    private final Data4Unit data4Unit;
    private final int timeDifference;

    public DataDownload(Data4Unit data4Unit) {
        super(Application.get(), data4Unit.attributes().size());
        this.data4Unit = data4Unit;
        timeDifference = data4Unit.unit().getSite().getTimeDifference();
    }

    @Override
    public void generateContent() throws Exception {
        StringList a = StringList.create(data4Unit.attributes());
        StoredObjectUtility.MethodList[] mls = StoredObjectUtility.createMethodLists(data4Unit.dataClass(), a);
        String h;
        for(int i = 0; i < mls.length; i++) {
            h = a.get(i);
            setValue(i, h.substring(0, h.indexOf(" AS ")));
        }
        writeRow();
        DateTimeFormatter excelFriendly = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Predicate<Data> filter = data4Unit.sliceFilter();
        for(StoredObject so: StoredObject.list(data4Unit.dataClass(), data4Unit.condition(), "CollectedAt")
                .filter(filter)) {
            setValue(0, new Timestamp((long)mls[0].invoke(so, true) + timeDifference).toLocalDateTime()
                    .format(excelFriendly));
            Object v;
            for(int i = 1; i < mls.length; i++) {
                v = mls[i].invoke(so, true);
                if(v instanceof Boolean b) {
                    setValue(i, b ? 1 : 0);
                } else {
                    setValue(i, v);
                }
            }
            writeRow();
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
