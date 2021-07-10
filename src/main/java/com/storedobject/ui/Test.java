package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.Executable;
import com.storedobject.common.IO;
import com.storedobject.common.JSON;
import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFReport;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        //new TestTemplate().execute();
        new TestFields().execute();
        //new TestAlert().execute();
        //new UploadTest().execute();
        //new TFTest().execute();
        //new TTest(Application.get()).execute();
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
            Chart soChart = new Chart();
            soChart.setSize("800px", "500px");

            // Generating some random values for a LineChart
            Random random = new Random();
            Data xValues = new Data();
            Data yValues1 = new Data(), yValues2 = new Data();
            for(int x = 0; x < 12; x++) {
                xValues.add(2020);
                yValues1.add(random.nextInt(100));
                yValues2.add(random.nextInt(100));
            }
            xValues.setName("Months of 2021");
            yValues1.setName("Random Values");

            // Bar charts is initialized with the generated XY values
            BarChart barChart1 = new BarChart(xValues, yValues1);
            barChart1.setName("Bar #1");
            barChart1.setStackName("BC"); // Just a name - should be same for all the charts on the same stack
            BarChart barChart2 = new BarChart(xValues, yValues2);
            barChart2.setName("Bar #2");
            barChart2.setStackName("BC"); // Just a name - should be same for all the charts on the same stack

            RadiusAxis radiusAxis = new RadiusAxis(xValues);
            radiusAxis.setMinAsMinData();
            radiusAxis.setMaxAsMaxData();
            AngleAxis angleAxis = new AngleAxis(yValues1);
            angleAxis.setMinAsMinData();
            angleAxis.setMaxAsMaxData();
            PolarCoordinate pc = new PolarCoordinate(radiusAxis, angleAxis);

            barChart1.plotOn(pc);
            //barChart2.plotOn(pc);

            soChart.add(pc);

            // Set the component for the view
            setComponent(new VerticalLayout(soChart));
        }
    }

    private static class Chart extends SOChart {

        @Override
        protected String customizeJSON(String json) {
            System.err.println("RAW");
            System.err.println(json);
            System.err.println("PRETTY");
            json = JSON.create(json).toPrettyString();
            System.err.println(json);
            return json;
        }
    }

    public static class TTest extends PDFReport {

        public TTest(Application application) {
            super(application);
        }

        @Override
        public void generateContent() throws Exception {
            ObjectTable<Person> persons = new ObjectTable<>(Person.class);
            for(Person person: StoredObject.list(Person.class)) {
                persons.addObject(person);
            }
            add(persons);
            PDFCell cell = createCell(Signature.get(((Application)getDevice()).getTransactionManager().getUser()));
            cell.setFixedHeight(50);
            cell.setBorder(0);
            add(cell);
        }
    }
}