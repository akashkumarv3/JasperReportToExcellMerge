package com.javahowtos.jasperdemo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.javahowtos.jasperdemo.beans.Ifsp;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import utils.DatabaseConnectionForJapserFill;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import net.sf.jasperreports.engine.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.repo.Resource;
import service.ExcelMergeService;





@RestController
public class JasperDemoController {

	private final ExcelMergeService excelMergeService =new ExcelMergeService();

	
	private static final String RESOURCE_DIRECTORY_PATH = "src/main/resources/uploaded-jrxml-reports";
    private static final String GENERATED_DIRECTORY_PATH = "src/main/resources";
    
    private static final String RESOURCE_XLSX_DIRECTORY_PATH = "src/main/resources/generated-xlsx-reports";
    private static final String GENERATED_MERGED_XLSX_DIRECTORY_PATH = "src/main/resources/genearted-merged-xlsx-reports";

    private static final String JASPER_FOLDER = "generated-jasper-reports/";
    private static final String XLSX_FOLDER = "generated-xlsx-reports/";
    private static final String JRXML_TEMP_FOLDER = "generated-temp-jrxml-reports/";
   

    @PostMapping("api/document")
    public void getDocument(HttpServletResponse response) throws IOException, JRException {
        File resourceDir = new File(RESOURCE_DIRECTORY_PATH);
        File[] jrxmlFiles = resourceDir.listFiles((dir, name) -> name.endsWith(".jrxml"));

        if (jrxmlFiles != null) {
            for (File jrxmlFile : jrxmlFiles) {
                String fileName = jrxmlFile.getName();
                String jasperFile = GENERATED_DIRECTORY_PATH + "/"+ JASPER_FOLDER + fileName.replace(".jrxml", ".jasper");
                String excelFile = GENERATED_DIRECTORY_PATH + "/" + XLSX_FOLDER + fileName.replace(".jrxml", ".xlsx");

                // Preprocess JRXML file to handle problematic <property> elements
                String tempJrxmlFile = GENERATED_DIRECTORY_PATH + "/" +JRXML_TEMP_FOLDER + fileName;
                preprocessJRXML(jrxmlFile.getAbsolutePath(), tempJrxmlFile);

                // Compile JRXML to Jasper
                JasperCompileManager.compileReportToFile(tempJrxmlFile, jasperFile);

                // Database connection parameters
                String jdbcUrl = "jdbc:mysql://localhost:3306/btc_param";
                String jdbcUser = "root";
                String jdbcPassword = "root";

                // Database connection
                try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)) {
                    // Create parameters map if needed
                    Map<String, Object> parameters = new HashMap<>();

                    // Fill the report with data
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperFile, parameters, connection);

                    // Export to Excel
                    net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter exporter = new net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter();
                    ExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
                    ExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(excelFile);

                    exporter.setExporterInput(exporterInput);
                    exporter.setExporterOutput((OutputStreamExporterOutput) exporterOutput);
                    exporter.exportReport();

                    System.out.println("Report generated successfully for file: " + fileName);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No JRXML files found in the directory.");
        }
        //now merge all excell file 
        
		  // Specify the directory containing the uploaded Excel files Path
		 Path resourceDirectory = Paths.get(RESOURCE_XLSX_DIRECTORY_PATH);
		  
		  // List all Excel files in the directory 
		 File[] excelFiles = excelMergeService.getExcelFiles(resourceDirectory);
		  
		  // Save merged file to src/main/resources/generated-reports folder
		  Path generatedDirectory = Paths.get(GENERATED_MERGED_XLSX_DIRECTORY_PATH); 
		  Path mergedFilePath = generatedDirectory.resolve("mergedExcel.xlsx");
		  
		  try { Workbook mergedWorkbook = new XSSFWorkbook();
		  
		  // Merge each Excel file
		  for (File excelFile : excelFiles) {
			  excelMergeService.mergeExcelFile(excelFile, mergedWorkbook);
		  }
		  
		  try (FileOutputStream fileOut = new
		  FileOutputStream(mergedFilePath.toFile())) { mergedWorkbook.write(fileOut);
		  System.out.println("Merged Excel file created successfully at " +
		  mergedFilePath.toAbsolutePath()); }
		  
		  mergedWorkbook.close(); 
		  }catch(IOException e)
	{
		e.printStackTrace();
	}
		 
        
        //ends here merging
    }

    private static void preprocessJRXML(String inputFilePath, String outputFilePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8);

        Pattern pattern = Pattern.compile("<property\\s+name=\"([^\"]*)\">\\s*<!\\[CDATA\\[([^]]*)]]>\\s*</property>");
        Matcher matcher = pattern.matcher(content);

        StringBuffer correctedContent = new StringBuffer();

        while (matcher.find()) {
            String propertyName = matcher.group(1).trim();
            String propertyValue = matcher.group(2).trim();
            matcher.appendReplacement(correctedContent, "<property name=\"" + propertyName + "\" value=\"" + propertyValue + "\"/>");
        }
        matcher.appendTail(correctedContent);

        Files.write(Paths.get(outputFilePath), correctedContent.toString().getBytes(StandardCharsets.UTF_8));
    }


}
