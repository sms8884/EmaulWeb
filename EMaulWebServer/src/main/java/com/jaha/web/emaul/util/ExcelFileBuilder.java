package com.jaha.web.emaul.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * <pre>
 * Class Name : ExcelFileBuilder.java
 * Description : 엑셀파일 생성 유틸
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2017. 1. 10.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2017. 1. 10.
 * @version 1.0
 */
public class ExcelFileBuilder {

    /**  */
    protected final Workbook workbook = new HSSFWorkbook();
    /**  */
    protected Sheet sheet;
    /**  */
    protected CellStyle normalStyle;
    /**  */
    protected CellStyle dateStyle;
    /**  */
    protected CellStyle timestampStyle;
    /**  */
    protected CellStyle integerStyle;
    /**  */
    protected CellStyle doubleStyle;
    /**  */
    protected CellStyle boolStyle;
    /**  */
    protected CellStyle headerStyle;
    /**  */
    protected int rowNum = 1;
    /**  */
    protected int headerCol = 0;
    /**  */
    private Map<Class<?>, HashMap<Color, CellStyle>> cellStyleMap;
    /**  */
    private HSSFPatriarch drawingPatriach;
    /**  */
    private CreationHelper createHelper;

    public enum Color {
        BLACK(HSSFColor.BLACK.index), RED(HSSFColor.RED.index), ORANGE(HSSFColor.ORANGE.index), LIGHT_ORANGE(HSSFColor.LIGHT_ORANGE.index), BLUE(HSSFColor.BLUE.index), YELLOW(HSSFColor.YELLOW.index), TURQUOISE(
                HSSFColor.TURQUOISE.index), DARK_YELLOW(HSSFColor.DARK_YELLOW.index), BRIGHT_GREEN(HSSFColor.BRIGHT_GREEN.index);

        private short index;

        private Color(short index) {
            this.index = index;
        }

        public short getIndex() {
            return this.index;
        }
    }

    public ExcelFileBuilder() {
        // Create styles for cell values
        normalStyle = workbook.createCellStyle();
        normalStyle.setWrapText(true);

        //
        timestampStyle = workbook.createCellStyle();
        timestampStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        //
        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        //
        integerStyle = workbook.createCellStyle();
        integerStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
        //
        doubleStyle = workbook.createCellStyle();
        doubleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        //
        boolStyle = workbook.createCellStyle();

        // Style for the header
        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        headerStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        headerStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        headerStyle.setBorderRight(CellStyle.BORDER_MEDIUM);

        // Header font
        final Font font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(font);

        // Error font
        final Font errorFont = workbook.createFont();
        errorFont.setColor(HSSFColor.WHITE.index);
        errorFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        errorFont.setFontHeightInPoints((short) 10);

        // Initialize the cellStyleMap
        cellStyleMap = new HashMap<Class<?>, HashMap<Color, CellStyle>>();
        cellStyleMap.put(String.class, new HashMap<Color, CellStyle>());
        cellStyleMap.put(Calendar.class, new HashMap<Color, CellStyle>());
        cellStyleMap.put(Integer.class, new HashMap<Color, CellStyle>());
        cellStyleMap.put(Double.class, new HashMap<Color, CellStyle>());
        cellStyleMap.put(Boolean.class, new HashMap<Color, CellStyle>());
        cellStyleMap.put(Timestamp.class, new HashMap<Color, CellStyle>());

        // Store the mapping for normal fields
        cellStyleMap.get(String.class).put(null, this.normalStyle);
        cellStyleMap.get(Calendar.class).put(null, this.normalStyle);
        cellStyleMap.get(Integer.class).put(null, this.normalStyle);
        cellStyleMap.get(Double.class).put(null, this.normalStyle);
        cellStyleMap.get(Boolean.class).put(null, this.normalStyle);

        // Add format for datetime
        {
            final CellStyle timestampStyle = workbook.createCellStyle();
            timestampStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
            cellStyleMap.get(Timestamp.class).put(null, timestampStyle);
        }

        // Store the mapping for colored fields
        for (Color color : Color.values()) {
            CellStyle normalColorStyle = copyAndColor(normalStyle, errorFont, color);
            cellStyleMap.get(String.class).put(color, normalColorStyle);

            CellStyle dateColorStyle = copyAndColor(dateStyle, errorFont, color);
            cellStyleMap.get(Calendar.class).put(color, dateColorStyle);

            CellStyle integerColorStyle = copyAndColor(integerStyle, errorFont, color);
            cellStyleMap.get(Integer.class).put(color, integerColorStyle);

            CellStyle doubleColorStyle = copyAndColor(doubleStyle, errorFont, color);
            cellStyleMap.get(Double.class).put(color, doubleColorStyle);

            CellStyle boolColorStyle = copyAndColor(boolStyle, errorFont, color);
            cellStyleMap.get(Boolean.class).put(color, boolColorStyle);

            CellStyle timestampColorStyle = copyAndColor(timestampStyle, errorFont, color);
            cellStyleMap.get(Timestamp.class).put(color, timestampColorStyle);
        } // end-for

        createHelper = workbook.getCreationHelper();
        if (sheet != null) {
            drawingPatriach = (HSSFPatriarch) sheet.createDrawingPatriarch();
        }
    }

    public ExcelFileBuilder(String sheetName) {
        this();
        newSheet(sheetName);
        if (sheet != null) {
            drawingPatriach = (HSSFPatriarch) sheet.createDrawingPatriarch();
        }
    }

    /**
     * Add a new sheet in the excel file. The given sheet name is cleaned (removing forbidden characters : /,\,*,?,[,],:,!) and truncated if necessary (max length = 31 characters).
     *
     * @param sheetName
     */
    public void newSheet(String sheetName) {
        sheet = workbook.createSheet(cleanSheetName(sheetName));
        rowNum = 1;
        headerCol = 0;
    }

    public void setHeaders(String... headers) {
        for (String h : headers) {
            addHeader(h);
        } // end-for
    }

    /**
     * Default column adding method
     *
     * @param header the label of the column header
     */
    public void addHeader(String header) {
        // The "+3" is arbitrary, but "* 256" is forced (see Javadoc)
        addHeader(header, (short) (256 * (header.length() + 3)));
    }

    /**
     * Advanced column adding method: the width can be forced. Beware that the width units are HSSF units, they can be weird. 0 is an acceptable value.
     *
     * @param header : the label of the header
     * @param width : the HSSF width, 0 is possible
     */
    public void addHeader(String header, int width) {
        // A header is always at line 0
        final Row row = getRow(0);
        final Cell headerCell = row.createCell(headerCol);
        headerCell.setCellValue(header);
        headerCell.setCellStyle(headerStyle);
        sheet.setColumnWidth(headerCol, width);
        ++headerCol;
    }

    public void setDataValue(int col, Calendar value) {
        final Cell cell = getCell(rowNum, col);
        if (value != null) {
            cell.setCellValue(value);
        } // end-if
        cell.setCellStyle(dateStyle);
    }

    public void setDataValue(int col, String value) {
        if (value != null && !"".equals(value)) {
            final Cell cell = getCell(rowNum, col);
            cell.setCellValue(value);
            cell.setCellStyle(normalStyle);
        } // end-if
    }

    public void setDataValue(int col, Double value) {
        final Cell cell = getCell(rowNum, col);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } // end-if
        cell.setCellStyle(doubleStyle);
    }

    public void setDataValue(int col, Integer value) {
        final Cell cell = getCell(rowNum, col);
        if (value != null) {
            cell.setCellValue(value.intValue());
        } // end-if
        cell.setCellStyle(integerStyle);
    }

    public void setDataValue(int col, boolean value) {
        final Cell cell = getCell(rowNum, col);
        cell.setCellValue(value);
        cell.setCellStyle(boolStyle);
    }

    public void nextRow() {
        ++rowNum;
    }

    public void adaptWidthToContents() {
        adaptWidthToContents(false);
    }

    public void adaptWidthToContents(boolean ignoreHeader) {
        final int colNb = getRow(0).getLastCellNum() + 1;
        outer: for (int c = 0; c < colNb; ++c) {
            int maxWidth = 0;
            final int startRowNum = ignoreHeader ? 1 : 0;
            for (int r = startRowNum; r <= rowNum; ++r) {
                final Cell cell = getCell(r, c);
                // Ignore columns corresponding to non-string values
                if (cell.getCellType() != Cell.CELL_TYPE_STRING && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                    continue outer;
                }
                if (maxWidth < cell.getStringCellValue().length()) {
                    maxWidth = cell.getStringCellValue().length();
                }
            } // end-for
            sheet.setColumnWidth(c, getColumnWidth(maxWidth));
        } // end-for
    }

    public void write(OutputStream oStream) throws IOException {
        workbook.write(oStream);
    }

    public void save(String fileName) throws Exception {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            boolean res = file.getParentFile().mkdirs();
            if (!res) {
                throw new Exception("Cannot create directory: " + file.getParentFile().getAbsolutePath());
            }
        } // end-if

        final FileOutputStream oStream = new FileOutputStream(fileName);
        try {
            write(oStream);
        } finally {
            if (oStream != null) {
                oStream.flush();
                oStream.close();
            }
        }
    }

    public void save(File file) throws Exception {
        final FileOutputStream oStream = new FileOutputStream(file);
        try {
            write(oStream);
        } finally {
            if (oStream != null) {
                oStream.flush();
                oStream.close();
            }
        }
    }

    public void autoSizeColumns() {
        for (int i = 0; i < 200; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (sheet.getColumnWidth(i)) + 1000);
        } // end-for
    }

    public void setDataValue(int col, Calendar value, boolean withTime, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment((withTime) ? Timestamp.class : Calendar.class, color, getCell(rowNum, col), comment);
    }

    public void setDataValue(int col, Calendar value, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment(Calendar.class, color, getCell(rowNum, col), comment);
    }

    public void setDataValue(int col, String[] values, Color color, String comment) {
        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < values.length; i++) {
            if (values != null) {
                value.append(values[i]).append(" - ");
            }
        } // end-for

        this.setDataValue(col, value.toString());
        setStyleAndComment(String.class, color, getCell(rowNum, col), comment);
    }


    public void setDataValue(int col, String value, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment(String.class, color, getCell(rowNum, col), comment);
    }

    public void setDataValue(int col, Double value, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment(Double.class, color, getCell(rowNum, col), comment);
    }

    public void setDataValue(int col, Integer value, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment(Integer.class, color, getCell(rowNum, col), comment);
    }

    public void setDataValue(int col, Boolean value, Color color, String comment) {
        this.setDataValue(col, value);
        setStyleAndComment(Boolean.class, color, getCell(rowNum, col), comment);
    }

    private CellStyle copyAndColor(CellStyle from, Font errorFont, Color color) {
        final CellStyle to = workbook.createCellStyle();
        to.cloneStyleFrom(from);
        to.setFillForegroundColor(color.getIndex());
        to.setFillPattern(CellStyle.SOLID_FOREGROUND);
        to.setFont(errorFont);
        return to;
    }

    private CellStyle getCellStyle(Class<?> clazz, Color color) {
        if (color != null) {
            return this.cellStyleMap.get(clazz).get(color);
        } else {
            return this.cellStyleMap.get(clazz).get(Color.BLACK);
        }
    }

    private void setStyleAndComment(Class<?> clazz, Color color, final Cell cell, String comment) {
        if (comment != null) {
            setComment(cell, comment);
        }
        cell.setCellStyle(getCellStyle(clazz, color));
    }

    private void setComment(Cell cell, String textCellComment) {
        HSSFClientAnchor anchor = (HSSFClientAnchor) this.createHelper.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 5);
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 5);

        Comment cellComment = drawingPatriach.createComment(anchor);
        cellComment.setString(createHelper.createRichTextString(textCellComment));
        cellComment.setAuthor("Huseyin OZVEREN");
        cell.setCellComment(cellComment);
    }

    private Row getRow(int row) {
        // Get or createthe row
        Row line = sheet.getRow(row);
        if (line == null) {
            line = sheet.createRow(row);
        }
        return line;
    }

    private Cell getCell(int row, int col) {
        final Row line = getRow(row);
        // Get or create the cell
        Cell cell = line.getCell(col);
        if (cell == null) {
            cell = line.createCell(col);
        }

        return cell;
    }

    private String cleanSheetName(String sheetName) {
        // Remove forbidden characters (/,\,*,?,[,],:,!) from sheet name.
        final String cleanName = sheetName.replaceAll("[/\\\\\\*\\?\\[\\]:!]", " ");
        if (cleanName.length() > 31) {
            return cleanName.substring(0, 31); // Sheet name length is limited to 31 characters
        }
        return cleanName;
    }

    private int getColumnWidth(int maxWidth) {
        // The max column width is 255*256, see the POI documentation
        return Math.min(256 * (maxWidth + 3), 255 * 256);
    }
}
