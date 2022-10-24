package sr.we.data.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Service
public class MyReportEngine {

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String un;

	@Value("${spring.datasource.password}")
	private String pw;

	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	public byte[] exportInvoice(Map<String, Object> map) throws JRException {
		InputStream employeeReportStream = MyReportEngine.class.getResourceAsStream("/reports/Invoice.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
		JRSaver.saveObject(jasperReport, "invoice.jasper");

		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(jdbcUrl, un, pw);
		} catch (SQLException | ClassNotFoundException ignored) {
		}
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, conn);

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String fileName = "invoice" + date + ".pdf";
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fileName));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("SeaQns");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		File fileInputStream = new File(fileName);
		byte[] readFileToByteArray = null;
		try {
			readFileToByteArray = FileUtils.readFileToByteArray(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readFileToByteArray;
	}

	public byte[] exportReceipt(Map<String, Object> map) throws JRException {
		InputStream employeeReportStream = MyReportEngine.class.getResourceAsStream("/reports/Bon.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
		JRSaver.saveObject(jasperReport, "receipt.jasper");

		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(jdbcUrl, un, pw);
		} catch (SQLException | ClassNotFoundException ignored) {
		}
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, conn);

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String fileName = "receipt" + date + ".pdf";
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fileName));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("SeaQns");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		File fileInputStream = new File(fileName);
		byte[] readFileToByteArray = null;
		try {
			readFileToByteArray = FileUtils.readFileToByteArray(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readFileToByteArray;
	}


}
