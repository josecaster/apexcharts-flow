package sr.we.data.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

//@Service
public class MyReportEngine {

//    @Value("${spring.datasource.url}")
    private String jdbcUrl;

//    @Value("${spring.datasource.username}")
    private String un;

//    @Value("${spring.datasource.password}")
    private String pw;

//    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    public byte[] exportPdfReport(byte[] reportFile, String fileName, Map<String, Object> map) throws JRException {
        InputStream reportStream = new ByteArrayInputStream(reportFile);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(jdbcUrl, un, pw);
        } catch (SQLException | ClassNotFoundException ignored) {
        }

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, conn);

        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        fileName = fileName + ".pdf";
        SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(fileName);
        exporter.setExporterOutput(exporterOutput);

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
