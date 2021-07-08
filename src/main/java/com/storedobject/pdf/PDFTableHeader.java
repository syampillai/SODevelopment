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
        int i = 0;
        while(i < this.widths.length) {
            if(i >= widths.length) {
                break;
            }
            this.widths[i] = widths[i];
            ++i;
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
     * @return Newly crated table. The header part of the table will have already created.
     */
    public PDFTable createTable(PDF pdf) {
        return createTable(pdf, null);
    }

    /**
     * Create a {@link PDFTable} from this header.
     * @param pdf PDF to which the table will be added later. (This is required to retrieve the font information from
     *            it.)
     * @param cellCustomizer Cell customizer to customize each cell.
     * @return Newly crated table. The header part of the table will have already created as per the definition of this
     * header.
     */
    public PDFTable createTable(PDF pdf, Function<PDFCell, PDFCell> cellCustomizer) {
        PDFTable table = PDF.createTable(widths);
        for(int i = 0; i < widths.length; i++) {
            table.addCell(pdf.createCell(cellHeaders[i], horizontalAlignments[i], verticalAlignments[i],
                    cellCustomizer));
        }
        return table;
    }
}
