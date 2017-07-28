package com.jaha.web.emaul.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

public class PoiUtil {

    public static Map<String, CellStyle> getCellStyle(Workbook wb) {

        Map<String, CellStyle> result = new HashMap<String, CellStyle>();

        DataFormat df = wb.createDataFormat();

        CellStyle styleHeader = wb.createCellStyle();
        styleHeader.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
        styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        styleHeader.setBorderTop(CellStyle.BORDER_THIN);
        styleHeader.setTopBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleHeader.setBorderRight(CellStyle.BORDER_THIN);
        styleHeader.setRightBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleHeader.setBorderBottom(CellStyle.BORDER_THIN);
        styleHeader.setBottomBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleHeader.setBorderLeft(CellStyle.BORDER_THIN);
        styleHeader.setLeftBorderColor(HSSFColor.GREY_80_PERCENT.index);
        result.put("styleHeader", styleHeader);


        CellStyle styleStringValue = wb.createCellStyle();
        styleStringValue.setAlignment(CellStyle.ALIGN_CENTER);

        styleStringValue.setBorderTop(CellStyle.BORDER_THIN);
        styleStringValue.setTopBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleStringValue.setBorderRight(CellStyle.BORDER_THIN);
        styleStringValue.setRightBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleStringValue.setBorderBottom(CellStyle.BORDER_THIN);
        styleStringValue.setBottomBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleStringValue.setBorderLeft(CellStyle.BORDER_THIN);
        styleStringValue.setLeftBorderColor(HSSFColor.GREY_80_PERCENT.index);
        result.put("styleStringValue", styleStringValue);

        CellStyle styleNumberValue = wb.createCellStyle();
        styleNumberValue.setAlignment(CellStyle.ALIGN_RIGHT);
        styleNumberValue.setDataFormat(df.getFormat("#,##0"));

        styleNumberValue.setBorderTop(CellStyle.BORDER_THIN);
        styleNumberValue.setTopBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleNumberValue.setBorderRight(CellStyle.BORDER_THIN);
        styleNumberValue.setRightBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleNumberValue.setBorderBottom(CellStyle.BORDER_THIN);
        styleNumberValue.setBottomBorderColor(HSSFColor.GREY_80_PERCENT.index);
        styleNumberValue.setBorderLeft(CellStyle.BORDER_THIN);
        styleNumberValue.setLeftBorderColor(HSSFColor.GREY_80_PERCENT.index);
        result.put("styleNumberValue", styleNumberValue);

        CellStyle defaultStyleStringValueLeft = wb.createCellStyle();
        defaultStyleStringValueLeft.setAlignment(CellStyle.ALIGN_LEFT);
        result.put("defaultStyleStringValueLeft", defaultStyleStringValueLeft);

        return result;

    }

    public static String getCellValue(Cell cell) {
        String szValue = "";
        try {
            int nType = cell.getCellType();
            switch (nType) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                        szValue = sf.format(cell.getDateCellValue());
                    } else {
                        DecimalFormat df = new DecimalFormat("###");
                        szValue = df.format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    szValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    szValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    szValue = Byte.toString(cell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    szValue = cell.getCellFormula();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            szValue = "";
        }

        return szValue.trim();
    }


}
