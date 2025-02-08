package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.office.ExcelReport;
import com.storedobject.ui.Application;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;

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
        Collection<String> a = data4Unit.attributes();
        int row = 0;
        goToCell(0, row++);
        setCellValue("Site: " + data4Unit.unit().getSite().getName());
        goToCell(0, row++);
        setCellValue("Unit: " + data4Unit.unit().getName());
        goToCell(0, row++);
        a.forEach(s -> {
            setCellValue(s.substring(s.indexOf(" AS ") + 4));
            moveRight();
        });
        for(ResultSet rs: StoredObject.query(data4Unit.dataClass(), String.join(",", a), data4Unit.condition(),
                data4Unit.orderBy(), true)) {
            goToCell(0, row++);
            setCellValue(new Timestamp(rs.getLong(1) + timeDifference));
            getCell().setCellStyle(getDateTimeStyle());
            Object v;
            for(int i = 2; i <= a.size(); i++) {
                moveRight();
                v = rs.getObject(i);
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
