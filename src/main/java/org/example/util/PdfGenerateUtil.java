package org.example.util;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PdfGenerateUtil {
    private static final Logger LOGGER = Logger.getLogger(PdfGenerateUtil.class.getName());
    private static final String BASE_DIRECTORY = "Documents";

    public enum ReportType {
        ANNUAL_SALES("AnnualSalesReports", "AnnualSalesReport"),
        MONTHLY_SALES("MonthlySalesReport", "MonthlySalesReport"),
        DAILY_SALES("DailySalesReport", "DailySalesReport"),
        EMPLOYEE("", "EmployeeReport For"),
        PRODUCT("", "ProductReport For"),
        SUPPLIER("", "SupplierReport For"),
        BILL("", "LastBill");

        private final String subDirectory;
        private final String filePrefix;

        ReportType(String subDirectory, String filePrefix) {
            this.subDirectory = subDirectory;
            this.filePrefix = filePrefix;
        }
    }

    private static boolean generatePdf(String text, String filePath) {
        try {
            File directory = new File(filePath).getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph(text));
                return true;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, STR."Error generating PDF: \{e.getMessage()}", e);
            return false;
        }
    }

    private static String buildFilePath(ReportType reportType, LocalDate date) {
        StringBuilder path = new StringBuilder(BASE_DIRECTORY);

        if (!reportType.subDirectory.isEmpty()) {
            path.append("/").append(reportType.subDirectory);
        }

        path.append("/").append(reportType.filePrefix);

        switch (reportType) {
            case ANNUAL_SALES:
                path.append(" ").append(date.getYear());
                break;
            case MONTHLY_SALES:
                path.append(" ").append(date.getMonth()).append("-").append(date.getYear());
                break;
            case DAILY_SALES:
            case EMPLOYEE:
            case PRODUCT:
            case SUPPLIER:
                path.append(" ").append(date);
                break;
        }

        return path.append(".pdf").toString();
    }

    public static String createBill(String text) {
        String path = STR."\{BASE_DIRECTORY}/LastBill.pdf";
        return generatePdf(text, path) ? path : null;
    }

    public static boolean generateAnnualSalesReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.ANNUAL_SALES, date));
    }

    public static boolean generateMonthlySalesReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.MONTHLY_SALES, date));
    }

    public static boolean generateDailySalesReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.DAILY_SALES, date));
    }

    public static boolean generateEmployeeReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.EMPLOYEE, date));
    }

    public static boolean generateProductReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.PRODUCT, date));
    }

    public static boolean generateSupplierReport(String text, LocalDate date) {
        return generatePdf(text, buildFilePath(ReportType.SUPPLIER, date));
    }
}