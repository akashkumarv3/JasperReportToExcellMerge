package utils;


import net.sf.jasperreports.engine.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;



public class JasperReportFiller {
	 public static JasperPrint fillReport(JasperReport jasperReport, Connection connection) throws JRException {
	        Map<String, Object> parameters = new HashMap<>(); // Add any required parameters here
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
	        return jasperPrint;
	    }
}
