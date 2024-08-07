package utils;




import net.sf.jasperreports.engine.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class JasperReportExporter {
    public static void exportToExcel(JasperPrint jasperPrint, String outputPath) throws JRException, IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = (Sheet) workbook.createSheet("Report");

            List<JRPrintPage> pages = jasperPrint.getPages();
            int rowIndex = 0;

            // Iterate through pages
            for (JRPrintPage page : pages) {
                // Iterate through elements on the page
                for (JRPrintElement element : page.getElements()) {
                    if (element instanceof JRPrintText) {
                        JRPrintText text = (JRPrintText) element;
                        Row row =  sheet.createRow(rowIndex++);
                        Cell cell = row.createCell(0);
                        // Assuming getText() or similar method provides text content
                        cell.setCellValue(text.getFullText()); // Replace with appropriate method if necessary
                    } else if (element instanceof JRPrintImage) {
                        // Handle images if needed
                        JRPrintImage image = (JRPrintImage) element;
                        // Add image handling logic here if needed
                    }
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
                workbook.write(fileOut);
            }

            System.out.println("Report exported to Excel successfully.");
        }

    }

}
