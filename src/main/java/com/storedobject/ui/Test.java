package com.storedobject.ui;

import com.storedobject.svg.chart.Bars;
import com.storedobject.svg.chart.Chart;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.Svg;

import java.util.HashMap;
import java.util.Map;

public class Test extends DataForm implements CloseableView {

    private final P5 p5;

    public Test() {
        super("Test", false);
        add(p5 = new P5());
        add(new Svg(chart()));
    }

    @Override
    protected boolean process() {
        Map<String, Object> data = new HashMap<>();
        data.put("x", 150);
        data.put("y", 0);
        p5.updateParameter("data", data);
        return false;
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

    private static class P5 extends P5Canvas {

        P5() {
            String sketch = """
                    p.data = new Map();
                    p.data.set("x", 100);
                    p.data.set("y", 100);
                    p.setup = () => {
                        p.createCanvas(200, 200);
                    };
                    p.draw = () => {
                        p.background(255);
                        p.line(p.random(p.width), p.random(p.height), p.data.get("x"), p.data.get("y"));
                    };
                    """;
            setSketch(sketch);
        }
    }
}
