package com.storedobject.pdf;

import java.util.function.Function;

/**
 * Header-definition for a {@link PDFTable}.
 *
 * @author Syam
 */
public class PDFTableHeader {

    private final String[] cellHeaders;
    private final int[] widths, horizontalAlignments, verticalAlignments;
    private PDFTable table;
    private PDF pdf;
    private Function<PDFCell, PDFCell> cellCustomizer;

    /**
     * Constructor. Default relative widths of all columns are set to 10. By default all headers will be centered.
     * @param cellHeaders Cell headers.
     */
    public PDFTableHeader(String... cellHeaders) {
        this.cellHeaders = cellHeaders;
        int n = cellHeaders.length;
        widths = new int[n];
        horizontalAlignments = new int[n];
        verticalAlignments = new int[n];
        for(int i = 0; i < n; i++) {
            widths[i] = 10;
            horizontalAlignments[i] = PDFElement.ALIGN_CENTER;
            verticalAlignments[i] = PDFElement.ALIGN_MIDDLE;
        }
    }

    /**
     * Set horizontal alignment.
     * @param columnIndex Column index.
     * @param alignment Alignment,
     */
    public void setHorizontalAlignment(int columnIndex, int alignment) {
        horizontalAlignments[columnIndex] = alignment;
    }

    /**
     * Set vertical alignment.
     * @param columnIndex Column index.
     * @param alignment Alignment,
     */
    public void setVerticalAlignment(int columnIndex, int alignment) {
        verticalAlignments[columnIndex] = alignment;
    }

    /**
     * Set relative widths.
     * @param widths Relative widths.
     */
    public void setWidths(int... widths) {
        for(int i = 0; i < this.widths.length && i < widths.length; ++i) {
            this.widths[i] = widths[i];
        }
    }

    /**
     * Set the relative width of a specific column.
     * @param columnIndex Column index.
     * @param width Relative width.
     */
    public void setWidth(int columnIndex, int width) {
        widths[columnIndex] = width;
    }

    /**
     * Create a {@link PDFTable} from this header.
     * @param pdf PDF to which the table will be added later. (This is required to retrieve the font information from
     *            it.)
     * @return Newly crated table. The header part of the table will have already created as per the definition of this
     * header.
     */
    public PDFTable createTable(PDF pdf) {
        this.pdf = pdf;
        table = PDF.createTable(widths);
        for(int i = 0; i < widths.length; i++) {
            table.addCell(pdf.createCell(pdf.createTitleText(cellHeaders[i]), horizontalAlignments[i],
                    verticalAlignments[i], cellCustomizer));
        }
        table.setHeaderRows(1);
        return table;
    }

    /**
     * Add cells to the table that was created earlier.
     * @param cells Cells to add.
     */
    public void addRow(Object... cells) {
        addCells(0, cells);
    }

    /**
     * Add cells to the table that was created earlier.
     *
     * @param startingColumn Starting column at which cells to be added.
     * @param cells Cells to add.
     * @return Next column index where further cells can be added.
     */
    public int addCells(int startingColumn, Object... cells) {
        if(startingColumn < 0) {
            startingColumn = 0;
        }
        while(startingColumn >= cellHeaders.length) {
            startingColumn -= cellHeaders.length;
        }
        if(cells == null || cells.length == 0) {
            return startingColumn;
        }
        for(Object cell: cells) {
            if(cell == null) {
                cell = "";
            }
            table.addCell(pdf.createCell(cell, horizontalAlignments[startingColumn],
                    verticalAlignments[startingColumn]), cellCustomizer);
            if(++startingColumn >= cellHeaders.length) {
                startingColumn = 0;
            }
        }
        return startingColumn;
    }

    /**
     * Set a customizer for the cells that will be added later.
     *
     * @param cellCustomizer Cell customizer.
     */
    public void setCellCustomizer(Function<PDFCell, PDFCell> cellCustomizer) {
        this.cellCustomizer = cellCustomizer;
    }
}
