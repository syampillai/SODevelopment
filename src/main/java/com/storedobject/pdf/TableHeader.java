package com.storedobject.pdf;

import com.storedobject.office.Excel;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Header-definition for a {@link PDFTable}.
 *
 * @author Syam
 */
public class TableHeader {

    private final String[] cellHeaders;
    private final int[] widths, horizontalAlignments, verticalAlignments;
    private PDFTable table;
    private PDF pdf;
    private Function<PDFCell, PDFCell> cellCustomizer;

    /**
     * Constructor. Default relative widths of all columns are set to 10. By default all headers will be centered.
     * @param cellHeaders Cell headers.
     */
    public TableHeader(String... cellHeaders) {
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
     * Constructor. Default relative widths of all columns are set to 10. By default all headers will be centered.
     * @param cellHeaders Cell headers.
     */
    public TableHeader(Iterable<String> cellHeaders) {
        this(array(cellHeaders));
    }

    private static String[] array(Iterable<String> cellHeaders) {
        List<String> a;
        if(cellHeaders instanceof List) {
            a = (List<String>) cellHeaders;
        } else {
            a = new ArrayList<>();
            for(String s: cellHeaders) {
                a.add(s);
            }
        }
        int n = a.size();
        String[] ch = new String[n];
        int i = 0;
        for(String s: a) {
            ch[i++] = s;
        }
        return ch;
    }

    /**
     * Set caption for a specific column.
     * @param columnIndex Column index.
     * @param caption Caption to set.
     */
    public void setHeader(int columnIndex, String caption) {
        cellHeaders[columnIndex] = caption;
    }

    private int index(String colName) {
        int i = 0;
        for(String cn: cellHeaders) {
            if(cn.equals(colName)) {
                return i;
            }
            ++i;
        }
        return -1;
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
     * Set horizontal alignment.
     * @param columnName Column name.
     * @param alignment Alignment,
     */
    public void setHorizontalAlignment(String columnName, int alignment) {
        setHorizontalAlignment(index(columnName), alignment);
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
     * Set vertical alignment.
     * @param columnName Column name.
     * @param alignment Alignment,
     */
    public void setVerticalAlignment(String columnName, int alignment) {
        setVerticalAlignment(index(columnName), alignment);
    }

    /**
     * Set relative widths.
     * @param widths Relative widths.
     */
    public void setWidths(int... widths) {
        if(widths != null) {
            for(int i = 0; i < this.widths.length && i < widths.length; ++i) {
                this.widths[i] = widths[i];
            }
        }
    }

    /**
     * Set relative widths.
     * @param widths Relative widths.
     */
    public void setWidths(Iterable<Integer> widths) {
        if(widths != null) {
            int i = 0;
            for(Integer w: widths) {
                this.widths[i++] = w;
            }
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
     * Set the relative width of a specific column.
     * @param columnName Column name.
     * @param width Relative width.
     */
    public void setWidth(String columnName, int width) {
        setWidth(index(columnName), width);
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

    /**
     * Fill the header values into the {@link Excel} instance provided. Values will be filled from left to right,
     * starting from the current cell. Finally, cell position will be set to the next row of the starting cell.
     *
     * @param excel Excel instance to which values to be filled.
     */
    public void fillHeaderCells(Excel excel) {
        int r = excel.getRowIndex(), c = excel.getCellIndex();
        for(int i = 0; i < cellHeaders.length; i++) {
            excel.goToCell(c + i, r);
            excel.setCellValue(customCell(excel, i), cellHeaders[i]);
        }
        excel.goToCell(c, r + 1);
    }

    /**
     * Fill the cell values into the {@link Excel} instance provided. Values will be filled from left to right,
     * starting from the current cell. Finally, cell position will be set to the next row of the starting cell.
     *
     * @param excel Excel instance to which values to be filled.
     * @param cellValues Cell values to fill.
     */
    public void fillRow(Excel excel, Object... cellValues) {
        int r = excel.getRowIndex(), c = excel.getCellIndex();
        fillCells(excel, cellValues);
        excel.goToCell(c, r + 1);
    }

    /**
     * Fill the cell values into the {@link Excel} instance provided. Values will be filled from left to right,
     * starting from the current cell. Finally, cell position will be set to the next cell on the right.
     *
     * @param excel Excel instance to which values to be filled.
     * @param cellValues Cell values to fill.
     */
    public void fillCells(Excel excel, Object... cellValues) {
        if(cellValues != null && cellValues.length > 0) {
            int r = excel.getRowIndex(), c = excel.getCellIndex();
            for(int i = 0; i < cellValues.length; i++) {
                excel.goToCell(c + i, r);
                excel.setCellValue(customCell(excel, i), cellValues[i]);
            }
            excel.goToCell(c + 1, r);
        }
    }

    private Cell customCell(Excel excel, int index) {
        Cell cell = excel.getCell();
        switch(horizontalAlignments[index]) {
            case PDFElement.ALIGN_CENTER, PDFElement.ALIGN_MIDDLE -> cell.setCellStyle(excel.getCenteredStyle());
            case PDFElement.ALIGN_RIGHT -> cell.setCellStyle(excel.getRightAlignedStyle());
        }
        return cell;
    }
}
