package com.storedobject.ui;

import com.storedobject.pdf.*;

public class Test extends PDFReport {

    public Test() {
        super(Application.get());
    }

    @Override
    public void generateContent() {

        PDFTable table; // Variable ot hold the table

        // First table
        table = new PDFTable(1);
        table.addCell(createCell("Hello 1 from Table 1"));
        Text text = new Text(); // Create a text
        text.newLine(true)
                .newLine(true) // Force a couple of new lines
                .append("Hello") // Add some text
                .append(PDFColor.RED) // Switch the color to red
                .append(" in red!") // Show something in red
                .append(14) // Switch the font to 14 pt
                .append(" Font in 14 pt") // Show something in that font
                .newLine() // Add a new line
                .append("Something in blue!", PDFColor.BLUE) // Show something in blue
                .newLine() // New line again
                .append(12, PDFFont.BOLD) // Switch to 12 pt bold font
                .append(PDFColor.BLACK) // Switch to black color
                .append("I'm in BOLD, but size is 12 pt!") // Some text
                .newLine()
                .newLine(true)
                .newLine(true); // Add 3 new lines, 2 of them are forced
        table.addCell(createCell(text));
        table.addCell(createCell("Hello 2 from Table 2"));

        add(table); // Add the table to the page

        // Second table
        table = new PDFTable(1);
        table.addCell(createCenteredCell("Hello World")); // Add centered text

        addGap(30); // Add some gap

        add(table); // Add the second table to the page

        // Third table
        table = new PDFTable(4);

        // Simple cell with no customization
        PDFCell cell = createCell("Left");
        table.addCell(cell);

        // Customized cell with no border on the right side
        cell = createCell("Middle 1", c -> {
            c.setBorderWidthRight(0);
            return c;
        });
        table.addCell(cell);

        // Customized cell with no border on the left side and centered (horizontally and vertically)
        cell = createCell("Middle 2", c -> {
            c.setBorderWidthLeft(0);
            c.setHorizontalAlignment(PDFElement.ALIGN_CENTER);
            c.setVerticalAlignment(PDFElement.ALIGN_CENTER);
            c.setGrayFill(0.9f); // With 90% grey filling
            return c;
        });
        table.addCell(cell);

        // Simple cell with no customization
        cell = createCell("Right");
        table.addCell(cell);

        add(table); // Add the third table to the page
    }
}
