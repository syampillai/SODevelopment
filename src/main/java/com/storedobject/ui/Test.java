package com.storedobject.ui;

import com.storedobject.svg.chart.Bars;
import com.storedobject.svg.chart.Chart;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Test extends DataForm implements CloseableView {

    public Test() {
        super("Test", false);
        add(new InteractiveLessonView());
        add(new Svg(chart()));
    }

    @Override
    protected boolean process() {
        close();
        return true;
    }

    private static Chart chart() {
        Bars c = new Bars();
        c.setUnit("%");
        c.setLabelName("Categories");
        c.setValueName("Category");
        c.addValue("Category A", 10);
        c.addValue("Category B", 90);
        c.addValue("Category C", 45);
        c.addValue("Category D", 20);
        c.addValue("Category E", 30);
        c.addValue("Category F", 50);
        c.addValue("Category G", 15);
        c.addValue("Category H", 100);
        return c;
    }

    private static class InteractiveLessonView extends VerticalLayout {

        private InteractiveLessonView() {
            setSizeFull();

            // Rive Component for Biology (Visual / Vector State Machine)
            RiveCanvas cellDiagram = new RiveCanvas("/Rave Files/Electron-Shells.riv", "Main");
            cellDiagram.setHeight("400px");
            cellDiagram.setWidth("100%");
            cellDiagram.addLoadErrorListener(e -> Application.error(e.getMessage()));

            Button triggerMitosis = new Button("Trigger Mitosis", e -> cellDiagram.firePropertyTrigger("startMitosis"));
            // Add to Layout
            add(
                new H2("Biology Module: Cell Division"),
                cellDiagram,
                triggerMitosis
            );
        }
    }
}
