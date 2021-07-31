package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.Executable;
import com.storedobject.common.JSON;
import com.storedobject.core.Person;
import com.storedobject.core.Query;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import jdk.jshell.JShell;

import java.util.*;

public class Test implements Executable {

    @Override
    public void execute() {
        /*
        VerifyOTP v = new VerifyOTP(true, "+971508421301", "okay@oka.com",
                () -> Application.warning("One"),
                () -> Application.warning("Two"),
                () -> Application.warning("Three")
                );
        v.execute();
        */
        //new TestChart().execute();
        //new HeatmapChartDemo().execute();
        //new TestTemplate().execute();
        //new TestFields().execute();
        //new TestAlert().execute();
        //new UploadTest().execute();
        //new TFTest().execute();
        //new TTest(Application.get()).execute();
        //new ObjectList("com.storedobject.core.SystemUser|Person.Age/GT AS Age greater than").execute();
        new QG(StoredObject.query(Person.class, "Title,FirstName")).execute();
    }

    public static class TestFields extends DataForm {

        private final DateField df;
        private final TimestampField tf;
        private final DateTimePicker tp = new DateTimePicker("V Time");
        private BooleanRadioField brf;
        private final ChoiceField cf = new ChoiceField("Choose", new String[] { "One", "Two", "Three" });
        private final RateField bdf = new RateField("Rate");
        private final BooleanField bf = new BooleanField("Boolean");

        public TestFields() {
            super("Test", false);
            addField(bf);
            setRequired(bf);
            addField(bdf);
            setRequired(bdf);
            cf.setPlaceholder("Hello");
            addField(cf);
            addField(brf = new BooleanRadioField("Hello"));
            String caption = getApplication().getQueryParameter("caption");
            setCaption(caption != null && !caption.isEmpty() ? caption : "NO PARAM");
            PhoneField phoneField = new PhoneField("Phone");
            addField(phoneField);
            addField(df = new DateField("Date"));
            setRequired(df);
            //df.setValue(null);
            addField(tf = new TimestampField("Timestamp"));
            //tf.setValue(xxx());
            //setRequired(tf);
            addField(tp);
            QuantityField f1 = new QuantityField("Quantity");
            f1.addValueChangeListener(e -> message(e.getValue()));
            f1.setAllowedUnits("cm", "MT", "m3", "m");
            addField(f1, new WeightField("Weight"), new VolumeField("Volume"));
            ViewerLink v = new ViewerLink("Test");
            v.setTextContent("com.storedobject.ui.Test");
            v.setContrast();
            add(v);
            v = new ViewerLink("Another");
            add(new CompoundField(v));
            MoneyField mf = new MoneyField("Amount");
            mf.setAllowedCurrencies("USD", "EUR", "AED");
            addField(mf);
            phoneField.setAllowedCountries("AE", "IN");
        }

        @Override
        protected boolean process() {
            /*
            Date d = DateUtility.create(1800, 1, 1);
            System.err.println(d);
            System.err.println(d.getTime());
            System.err.println(new Timestamp(0));
            df.getEmptyValue();
            message("Date value is " + df.getValue());
            message("Time value is " + tf.getValue());
            setFieldReadOnly(isFieldEditable(df), df, tf);
            //tf.setValue(null);
            System.err.println("Got: " + tf.getValue());
            df.setValue(d);
             */
            System.err.println(bdf.getValue() + " " + bdf.getEmptyValue());
            return false;
        }
    }

    public static class TestTemplate extends TemplateView implements CloseableView {

        @Id
        private TextField name;
        @Id
        private com.vaadin.flow.component.html.H1 sree;
        @Id
        SOChart chart;

        public TestTemplate() {
            super("Test", "com.storedobject.ui.Test");
            name.addValueChangeListener(e -> sree.setText(name.getValue()));
        }

        @Override
        protected Component createComponentForId(String id) {
            if("name".equals(id)) {
                new TextField("Hello Sree");
            }
            return super.createComponentForId(id);
        }
    }

    public static class TestChart extends View implements CloseableView {

        public TestChart() {
            super("Chart");

            // Creating a chart display area
            CustomChart soChart = new CustomChart();
            soChart.setSize("800px", "500px");

            // Generating some random values for a LineChart
            Random random = new Random();
            CategoryData xValues = new CategoryData();
            Data yValues1 = new Data(), yValues2 = new Data();
            for(int x = 0; x <= 11; x++) {
                xValues.add("" + (2010 + x));
                yValues1.add(random.nextInt(100));
                yValues2.add(random.nextInt(100));
            }

            // Define axes
            XAxis xAxis = new XAxis(xValues);
            xAxis.setMinAsMinData();
            YAxis yAxis1 = new YAxis(yValues1), yAxis2 = new YAxis(yValues2);

            // Bar charts is initialized with the generated XY values
            BarChart barChart1 = new BarChart(xValues, yValues1);
            barChart1.setName("Wheat");
            BarChart barChart2 = new BarChart(xValues, yValues2);
            barChart2.setName("Rice");
            barChart2.setBarGap(0);

            // Create and customize value-labels of one of the charts
            Chart.Label label = barChart1.getLabel(true);
            label.setFormatter("{1} {black|{chart} Hello World}");
            label.setInside(true);
            label.setGap(15);
            label.setRotation(90);
            label.getPosition().bottom();
            Alignment alignment = label.getAlignment(true);
            alignment.alignCenter();
            alignment.justifyLeft();
            RichTextStyle rich = label.getRichTextStyle(true);
            TextStyle richText = rich.get("black", true);
            richText.setColor(new Color("black"));
            barChart2.setLabel(label);

            // Use a coordinate system
            RectangularCoordinate rc = new RectangularCoordinate();
            rc.addAxis(xAxis, yAxis1, yAxis2);
            barChart1.plotOn(rc);
            barChart2.plotOn(rc);

            soChart.add(rc);

            // Set the component for the view
            setComponent(new VerticalLayout(soChart));
        }
    }

    private static class CustomChart extends SOChart {

        @Override
        protected String customizeJSON(String json) {
            System.err.println("RAW");
            System.err.println(json);
            System.err.println("PRETTY");
            json = JSON.create(json).toPrettyString();
            System.err.println(json);
            return json;
        }

        @Override
        protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
            System.err.println("RAW DATA " + data.getSerial());
            System.err.println(json);
            System.err.println("PRETTY DATA " + data.getSerial());
            json = JSON.create(json).toPrettyString();
            System.err.println(json);
            return json;
        }
    }

    public static class M extends GridMenu {

        public M() {
            super("Menu Test");
        }
    }

    public static class SOShell extends DataForm {

        private final TextArea code = new TextArea("Code Shell");
        private final JShell shell;

        public SOShell() {
            super("Shell", false);
            addField(code);
            shell = JShell.create();
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Evaluate");
        }

        @Override
        protected boolean process() {
            String code = this.code.getValue();
            if(!code.isBlank()) {
                shell.eval(code);
                shell.variables().forEach(v -> {
                    message(v.name() + " = " + shell.varValue(v));
                });
            }
            return false;
        }
    }

    public static class HeatmapChartDemo extends View implements CloseableView {

        public HeatmapChartDemo() {
            super("Temperature");

            // Creating a chart display area
            SOChart soChart = new SOChart();
            soChart.setSize("800px", "500px");

            // Heatmap chart requires 2 category axes and then, values to be added for each data-point
            CategoryData days = new CategoryData("Sun", "Mon", "Wed", "Thu", "Fri", "Sat");
            CategoryData slots = new CategoryData("Morning", "Noon", "Afternoon", "Evening", "Night");

            //Create the chart.
            HeatmapChart chart = new HeatmapChart(days, slots);
            chart.getLabel(true).show(); // Want to display the value as labels

            // Add some data-points
            chart.addData(0, 0, 27); // Sunday morning
            chart.addData(0, 3, 28); // Sunday evening
            chart.addData(1, 3, 31); // Monday evening
            chart.addData(1, 4, 25); // Monday night
            chart.addData("Wed", "Noon", 37);

            // Heatmap charts should be plotted on a rectangular coordinate system
            chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(slots)));

            // Add to the chart display area
            soChart.add(chart);

            // Set the component for the view
            setComponent(new HorizontalLayout(soChart));
        }
    }

    public static class QG extends QueryGrid {

        public QG(Query query) {
            super(query);
        }
    }
}