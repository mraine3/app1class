package com.lessons.services;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service("com.lessons.service.ExcelService")
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);


    public byte[] generateExcelFileAsByteArray() throws Exception {
        logger.debug("generateExcelFileAsByteArray() started.");

        // Creating a Streaming Workbook instance
        //        Keep 100 rows in memory.  The remaining rows will be written to disk
        //        + This does not increase or decrease the time it takes to generate the XLSX file
        //        + This reduces memory consumption
        SXSSFWorkbook streamingWorkBook = new SXSSFWorkbook(100);

        // Create the worksheet (within this excel workbook)
        SXSSFSheet sheet = streamingWorkBook.createSheet("Sheet 1");

        // Create the styles (for various cells)
        CellStyle headerCellStyle      = getHeaderCellStyle(sheet);
        CellStyle greenHeaderCellStyle = getGreenHeaderCellStyle(sheet);
        CellStyle greenCellStyle = getBorderedGreenTextStyle(sheet);
        CellStyle dataCellStyle = getBorderAndCenteredTextStyle(sheet);

        // Set column widths
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 4000);

        // Row 1:  Title Row
        int currentRowNumber = 0;
        Row row = sheet.createRow(currentRowNumber);
        row.setHeight((short) 600);

        // Create the cells in this row
        CellUtil.createCell(row, 0, "Bogus Employee List", headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));    // Merge cells (0,0) to (0,3)

        // Row 2:  Note
        currentRowNumber++;
        row = sheet.createRow(currentRowNumber);
        row.setHeight((short) 400);
        CellUtil.createCell(row, 0, "NOTE: This shows the salaries of all employees in the company");

        // Row 3:  Empty row
        currentRowNumber++;


        // Row 4:  Header row
        currentRowNumber++;
        row = sheet.createRow(currentRowNumber);
        CellUtil.createCell(row, 0, "Employee ID",  headerCellStyle);
        CellUtil.createCell(row, 1, "Full Name",    headerCellStyle);
        CellUtil.createCell(row, 2, "Title",        headerCellStyle);
        CellUtil.createCell(row, 3, "Salary",       greenHeaderCellStyle);

        // Row 5:  Data row
        currentRowNumber++;
        row = sheet.createRow(currentRowNumber);
        CellUtil.createCell(row, 0, "1000",                 dataCellStyle);
        CellUtil.createCell(row, 1, "Emmet Brown",          dataCellStyle);
        CellUtil.createCell(row, 2, "Science & Technology", dataCellStyle);
        createCell(row,          3, 80000,                  greenCellStyle);


        // Row 6:  Data row
        currentRowNumber++;
        row = sheet.createRow(currentRowNumber);
        CellUtil.createCell(row, 0, "1001",        dataCellStyle);
        CellUtil.createCell(row, 1, "Marty McFly", dataCellStyle);
        CellUtil.createCell(row, 2, "Slacker",     dataCellStyle);
        createCell(row,          3, 30000,         greenCellStyle);

        // Save the information to the byteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingWorkBook.write(byteArrayOutputStream);

        // Dispose of the temporary file (used by the streaming workbook)
        streamingWorkBook.dispose();

        logger.debug("generateExcelFileAsByteArray() finished.");

        // Convert the byteArrayOutputStream into an array of bytes
        return byteArrayOutputStream.toByteArray();
    }


    private CellStyle getBorderedGreenTextStyle(SXSSFSheet aSheet) {

        CellStyle style = aSheet.getWorkbook().createCellStyle();
        style.setBorderBottom(BorderStyle.HAIR);
        style.setBorderLeft(BorderStyle.HAIR);
        style.setBorderRight(BorderStyle.HAIR);
        style.setBorderTop(BorderStyle.HAIR);
        style.setAlignment(HorizontalAlignment.CENTER);

        // Get the color index closes to the RGB coordinates
        short colorIndexForGreen = getGreenColorIndex(aSheet);

        style.setFillForegroundColor(colorIndexForGreen);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font boldFont11 = aSheet.getWorkbook().createFont();
        boldFont11.setBold(true);
        boldFont11.setFontHeightInPoints((short) 11);
        boldFont11.setFontName("Calibri");
        style.setFont(boldFont11);

        return style;
    }

    public short getGreenColorIndex(SXSSFSheet aSheet) {
        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFPalette palette = hwb.getCustomPalette();
        HSSFColor myColor = palette.findSimilarColor(224, 224, 180);
        short colorIndexForGreen = myColor.getIndex();

        return colorIndexForGreen;
    }


    private CellStyle getBorderAndCenteredTextStyle(SXSSFSheet aSheet) {
        CellStyle style = aSheet.getWorkbook().createCellStyle();
        style.setBorderBottom(BorderStyle.HAIR);
        style.setBorderLeft(BorderStyle.HAIR);
        style.setBorderRight(BorderStyle.HAIR);
        style.setBorderTop(BorderStyle.HAIR);

        // Center horizontally
        style.setAlignment(HorizontalAlignment.CENTER);

        // Vertically, push to the bottom
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return style;
    }


    private CellStyle getHeaderCellStyle(SXSSFSheet aSheet) {
        // Build the Header cell style
        CellStyle style = aSheet.getWorkbook().createCellStyle();
        Font font = aSheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // Center horizontally
        style.setAlignment(HorizontalAlignment.CENTER);

        // Vertically, push to the bottom
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return style;
    }


    private CellStyle getGreenHeaderCellStyle(SXSSFSheet aSheet) {
        // Build the Header cell style
        CellStyle style = aSheet.getWorkbook().createCellStyle();
        Font font = aSheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // Center horizontally
        style.setAlignment(HorizontalAlignment.CENTER);

        // Vertically, push to the bottom
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);

        // Get the color index closes to the RGB coordinates
        short colorIndexForGreen = getGreenColorIndex(aSheet);

        style.setFillForegroundColor(colorIndexForGreen);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }



    public void createCell(Row aRow, int aColumnNumber, Integer aValue, CellStyle aCellStyle) {
        Cell cell = aRow.createCell(aColumnNumber);
        cell.setCellValue(aValue);

        if (aCellStyle != null) {
            cell.setCellStyle(aCellStyle);
        }
    }


}
