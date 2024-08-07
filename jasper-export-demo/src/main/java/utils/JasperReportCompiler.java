package utils;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

import java.io.File;




public class JasperReportCompiler {
	 public static JasperReport compileReport(String jrxmlPath) throws Exception {
	        File jrxmlFile = new File(jrxmlPath);
	        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
	        return jasperReport;
	    }
}
