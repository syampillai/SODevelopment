package com.storedobject.ui;

import com.storedobject.core.ObjectCacheList;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;

import java.util.Random;

public class Test extends ObjectListGrid<Person> implements CloseableView {

    public Test() {
        super(Person.class);
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Load", e -> load()),
                new Button("Test", e -> test()),
                new Button("Sort", e -> sort())
        );
    }

    private void sort() {
        sort("FirstName");
        /*
        Time time = new Time();
        sort((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(p1.getFirstName(), p2.getFirstName()));
        System.err.println("Sorted in " + time.report() + " ms");

         */
    }

    private void test() {
        Time time = new Time();
        ObjectCacheList<Person> list = new ObjectCacheList<>(Person.class);
        list.load();
        message("Count: " + list.size() + ", Time: " + time.report());
        time.start();
        Person p = list.get(50000);
        time.start();
        message(p + ", Took: " + time.report());
    }

    private static class Time {

        private long time;
        private boolean running = false;

        Time() {
            start();
        }

        void start() {
            running = true;
            time = System.currentTimeMillis();
        }

        void stop() {
            if(!running) {
                return;
            }
            running = false;
            time = System.currentTimeMillis() - time;
        }

        long report() {
            return running ? (System.currentTimeMillis() - time) : time;
        }
    }
}
