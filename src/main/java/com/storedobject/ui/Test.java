package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Signature;
import com.storedobject.vaadin.View;
import org.vaadin.stefan.table.TableDataCell;

import java.util.function.Function;

public class Test extends View {

    public Test() {
        setCaption("Signatures");
        setComponent(new SignatureTable());
    }

    static class SignatureTable extends ObjectTable<Signature> {

        public SignatureTable() {
            super(Signature.class, StringList.create("Person", "Signature"));
            load();
        }

        @Override
        public Function<Signature, ?> getColumnFunction(String columnName) {
            if("Signature".equals(columnName)) {
                return s -> "";
            }
            return super.getColumnFunction(columnName);
        }

        @Override
        protected void customizeCell(String columnName, Signature signature, TableDataCell cell) {
            if("Signature".equals(columnName)) {
                Image image = new Image();
                image.setSource(signature);
                cell.add(image);
            }
        }
    }
}