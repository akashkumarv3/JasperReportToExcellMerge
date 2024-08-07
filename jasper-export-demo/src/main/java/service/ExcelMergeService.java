package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;





@Service
public class ExcelMergeService {

	  /**
     * Retrieves all Excel files from the specified directory.
     *
     * @param directory the directory to search for Excel files
     * @return an array of Excel files
     */
    public File[] getExcelFiles(Path directory) {
        return directory.toFile().listFiles((dir, name) -> name.endsWith(".xls") || name.endsWith(".xlsx"));
    }

    /**
     * Merges an Excel file into the provided workbook.
     *
     * @param excelFile      the Excel file to merge
     * @param mergedWorkbook the target workbook
     * @throws IOException if an I/O error occurs
     */
    public  void mergeExcelFile(File excelFile, Workbook mergedWorkbook) throws IOException {
        try (InputStream fileIn = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fileIn)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet inputSheet = workbook.getSheetAt(i);
                Sheet newSheet = mergedWorkbook.createSheet(inputSheet.getSheetName() + "_" + excelFile.getName());

                // Copy rows and cells
                copyRowsAndCells(inputSheet, newSheet, mergedWorkbook);

                // Copy column widths
                copyColumnWidths(inputSheet, newSheet);

                // Copy merged regions
                copyMergedRegions(inputSheet, newSheet);
            }
        }
    }

    
    public  void copyRowsAndCells(Sheet inputSheet, Sheet newSheet, Workbook targetWorkbook) {
        Map<CellStyle, CellStyle> styleMap = new HashMap<>();

        for (int rowIndex = 0; rowIndex <= inputSheet.getLastRowNum(); rowIndex++) {
            Row inputRow = inputSheet.getRow(rowIndex);
            if (inputRow != null) {
                Row newRow = newSheet.createRow(rowIndex);
                newRow.setHeight(inputRow.getHeight()); // Copy row height
                for (int cellIndex = 0; cellIndex < inputRow.getLastCellNum(); cellIndex++) {
                    Cell inputCell = inputRow.getCell(cellIndex);
                    Cell newCell = newRow.createCell(cellIndex);
                    if (inputCell != null) {
                        copyCellValueAndStyle(inputCell, newCell, targetWorkbook, styleMap);
                    }
                }
            }
        }
    }

    public  void copyColumnWidths(Sheet inputSheet, Sheet newSheet) {
        for (int columnIndex = 0; columnIndex < inputSheet.getRow(0).getLastCellNum(); columnIndex++) {
            newSheet.setColumnWidth(columnIndex, inputSheet.getColumnWidth(columnIndex)); // Copy column width
        }
    }
    public  void copyMergedRegions(Sheet inputSheet, Sheet newSheet) {
        for (int i = 0; i < inputSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = inputSheet.getMergedRegion(i);
            CellRangeAddress newMergedRegion = new CellRangeAddress(
                mergedRegion.getFirstRow(),
                mergedRegion.getLastRow(),
                mergedRegion.getFirstColumn(),
                mergedRegion.getLastColumn()
            );
            newSheet.addMergedRegion(newMergedRegion);
        }
    }


    public  void copyCellValueAndStyle(Cell inputCell, Cell newCell, Workbook targetWorkbook, Map<CellStyle, CellStyle> styleMap) {
        // Copy cell value
        switch (inputCell.getCellType()) {
            case STRING:
                newCell.setCellValue(inputCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(inputCell)) {
                    newCell.setCellValue(inputCell.getDateCellValue());
                } else {
                    newCell.setCellValue(inputCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                newCell.setCellValue(inputCell.getBooleanCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(inputCell.getCellFormula());
                break;
            case BLANK:
                newCell.setBlank();
                break;
            case ERROR:
                newCell.setCellErrorValue(inputCell.getErrorCellValue());
                break;
            default:
                break;
        }

        // Copy cell style
        CellStyle inputStyle = inputCell.getCellStyle();
        CellStyle targetStyle = styleMap.get(inputStyle);

        if (targetStyle == null) {
            targetStyle = targetWorkbook.createCellStyle();
            targetStyle.cloneStyleFrom(inputStyle);
            styleMap.put(inputStyle, targetStyle);
        }

        newCell.setCellStyle(targetStyle);
    }

    
    
    //second approch here form jasper to merged excell report
    
    //ends jasper to excell
}
