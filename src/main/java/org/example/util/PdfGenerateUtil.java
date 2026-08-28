package org.example.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.example.entity.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfGenerateUtil {
    private static final Logger LOGGER = Logger.getLogger(PdfGenerateUtil.class.getName());
    public static final String BASE_DIRECTORY = "Documents";
    public static final String INVOICES_DIRECTORY = "Documents/Invoices";
    public static final String REPORTS_DIRECTORY = "Documents/Reports";

    private static final DeviceRgb THEME_COLOR = new DeviceRgb(220, 80, 50); // TeeCeylon Coral/Red
    private static final DeviceRgb HEADER_BG = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb ACCENT_BG = new DeviceRgb(238, 242, 246);

    private static void ensureDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Generates a professional branded invoice PDF for a customer order.
     */
    public static String generateOrderInvoice(OrderEntity order, List<OrderDetailsEntity> details) {
        ensureDirectory(INVOICES_DIRECTORY);
        String fileName = INVOICES_DIRECTORY + "/" + order.getOrdId() + ".pdf";
        String lastBill = BASE_DIRECTORY + "/LastBill.pdf";

        try {
            createInvoiceDocument(order, details, fileName);
            createInvoiceDocument(order, details, lastBill);
            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate order invoice: " + e.getMessage(), e);
            return null;
        }
    }

    private static void createInvoiceDocument(OrderEntity order, List<OrderDetailsEntity> details, String filePath) throws Exception {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            // Store Header Banner
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
            
            Cell brandCell = new Cell().setBorder(Border.NO_BORDER);
            brandCell.add(new Paragraph("TEECEYLON").setFontSize(22).setBold().setFontColor(THEME_COLOR));
            brandCell.add(new Paragraph("Premium T-Shirt & Apparel Store").setFontSize(10).setItalic());
            brandCell.add(new Paragraph("123 Galle Road, Colombo 03, Sri Lanka\nHotline: +94 11 234 5678 | Email: sales@teeceylon.lk").setFontSize(9));
            headerTable.addCell(brandCell);

            Cell invMetaCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            invMetaCell.add(new Paragraph("SALES INVOICE").setFontSize(16).setBold().setFontColor(ColorConstants.DARK_GRAY));
            invMetaCell.add(new Paragraph("Invoice No: " + order.getOrdId()).setFontSize(11).setBold());
            invMetaCell.add(new Paragraph("Date: " + (order.getOrdDate() != null ? order.getOrdDate() : LocalDate.now())).setFontSize(9));
            invMetaCell.add(new Paragraph("Time: " + (order.getOrdTime() != null ? order.getOrdTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "")).setFontSize(9));
            headerTable.addCell(invMetaCell);

            document.add(headerTable);
            document.add(new Paragraph("\n").setFontSize(5));

            // Customer Details Box
            Table custTable = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
            Cell custCell = new Cell().setBackgroundColor(ACCENT_BG).setPadding(8).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
            custCell.add(new Paragraph("BILL TO:").setFontSize(10).setBold());
            String custName = order.getCusName() != null && !order.getCusName().isEmpty() ? order.getCusName() : "Walk-in Customer";
            String custPhone = order.getCusPhone() != null ? order.getCusPhone() : "-";
            String custEmail = order.getCusEmail() != null ? order.getCusEmail() : "-";
            custCell.add(new Paragraph("Customer: " + custName + "    |    Contact: " + custPhone + "    |    Email: " + custEmail).setFontSize(9));
            custTable.addCell(custCell);
            document.add(custTable);

            document.add(new Paragraph("\n").setFontSize(5));

            // Order Items Table
            Table itemTable = new Table(UnitValue.createPercentArray(new float[]{8, 20, 26, 12, 10, 12, 12})).useAllAvailableWidth();

            String[] headers = {"#", "Item Code", "Description", "Size", "Qty", "Unit Price", "Total (Rs.)"};
            for (String h : headers) {
                itemTable.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold())
                        .setBackgroundColor(HEADER_BG)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
            }

            int index = 1;
            double grandTotal = 0.0;
            if (details != null) {
                for (OrderDetailsEntity item : details) {
                    ProductEntity p = item.getProduct();
                    String code = p != null && p.getSku() != null ? p.getSku() : item.getProId();
                    String name = p != null && p.getProName() != null ? p.getProName() : "T-Shirt";
                    String size = p != null && p.getProSize() != null ? p.getProSize() : "-";
                    int qty = item.getProQty() != null ? item.getProQty() : 1;
                    double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : (item.getProTotal() != null ? item.getProTotal() / qty : 0.0);
                    double lineTotal = item.getProTotal() != null ? item.getProTotal() : (unitPrice * qty);
                    grandTotal += lineTotal;

                    itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(index++)).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    itemTable.addCell(new Cell().add(new Paragraph(code).setFontSize(9)));
                    itemTable.addCell(new Cell().add(new Paragraph(name).setFontSize(9)));
                    itemTable.addCell(new Cell().add(new Paragraph(size).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(qty)).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    itemTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", unitPrice)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
                    itemTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", lineTotal)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
                }
            }

            document.add(itemTable);
            document.add(new Paragraph("\n").setFontSize(5));

            // Summary Table
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
            Cell noteCell = new Cell().setBorder(Border.NO_BORDER);
            noteCell.add(new Paragraph("Payment Status: PAID").setFontSize(10).setBold().setFontColor(new DeviceRgb(40, 160, 40)));
            noteCell.add(new Paragraph("Thank you for choosing TeeCeylon!\nPlease retain this invoice for exchange within 7 days.").setFontSize(8).setItalic());
            totalTable.addCell(noteCell);

            Cell sumCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            sumCell.add(new Paragraph("GRAND TOTAL: Rs. " + String.format("%.2f", order.getOrdTotal() != null ? order.getOrdTotal() : grandTotal))
                    .setFontSize(13).setBold().setFontColor(THEME_COLOR));
            totalTable.addCell(sumCell);

            document.add(totalTable);
        }
    }

    /**
     * Generates a Sales Report PDF.
     */
    public static String generateSalesReport(String periodTitle, List<OrderEntity> orders) {
        ensureDirectory(REPORTS_DIRECTORY);
        String fileName = REPORTS_DIRECTORY + "/SalesReport_" + LocalDate.now() + ".pdf";

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            document.add(new Paragraph("TEECEYLON - SALES REPORT").setFontSize(18).setBold().setFontColor(THEME_COLOR));
            document.add(new Paragraph("Period: " + periodTitle + " | Generated On: " + LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n").setFontSize(6));

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 25, 15, 10, 20})).useAllAvailableWidth();
            String[] headers = {"Order ID", "Date", "Customer", "Contact", "Items", "Amount (Rs.)"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold()).setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            }

            double totalSales = 0.0;
            int totalItems = 0;
            if (orders != null) {
                for (OrderEntity o : orders) {
                    int itemsCount = o.getOrderDetails() != null ? o.getOrderDetails().stream().mapToInt(d -> d.getProQty() != null ? d.getProQty() : 0).sum() : 0;
                    double amt = o.getOrdTotal() != null ? o.getOrdTotal() : 0.0;
                    totalSales += amt;
                    totalItems += itemsCount;

                    table.addCell(new Cell().add(new Paragraph(o.getOrdId()).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(o.getOrdDate() != null ? o.getOrdDate().toString() : "").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(o.getCusName() != null ? o.getCusName() : "-").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(o.getCusPhone() != null ? o.getCusPhone() : "-").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(itemsCount)).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", amt)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
                }
            }

            document.add(table);
            document.add(new Paragraph("\n").setFontSize(6));

            Paragraph summary = new Paragraph(String.format("Total Orders: %d  |  Total Items Sold: %d  |  Total Revenue: Rs. %.2f",
                    orders != null ? orders.size() : 0, totalItems, totalSales))
                    .setFontSize(11).setBold().setTextAlignment(TextAlignment.RIGHT);
            document.add(summary);

            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate sales report: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generates an Inventory Status Report PDF.
     */
    public static String generateInventoryReport(List<ProductEntity> products) {
        ensureDirectory(REPORTS_DIRECTORY);
        String fileName = REPORTS_DIRECTORY + "/InventoryReport_" + LocalDate.now() + ".pdf";

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            document.add(new Paragraph("TEECEYLON - INVENTORY REPORT").setFontSize(18).setBold().setFontColor(THEME_COLOR));
            document.add(new Paragraph("Generated On: " + LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n").setFontSize(6));

            Table table = new Table(UnitValue.createPercentArray(new float[]{10, 16, 24, 12, 10, 14, 14})).useAllAvailableWidth();
            String[] headers = {"ID", "SKU", "Product Name", "Category", "Size", "Unit Price", "Stock Qty"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold()).setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            }

            int totalStock = 0;
            double totalValuation = 0.0;
            if (products != null) {
                for (ProductEntity p : products) {
                    int qty = p.getStockQty() != null ? p.getStockQty() : 0;
                    double price = p.getProPrice() != null ? p.getProPrice() : 0.0;
                    totalStock += qty;
                    totalValuation += (qty * price);

                    table.addCell(new Cell().add(new Paragraph(p.getProId()).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(p.getSku() != null ? p.getSku() : "-").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(p.getProName() != null ? p.getProName() : "-").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(p.getProCategory() != null ? p.getProCategory() : "-").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(p.getProSize() != null ? p.getProSize() : "-").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", price)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(qty)).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                }
            }

            document.add(table);
            document.add(new Paragraph("\n").setFontSize(6));

            Paragraph summary = new Paragraph(String.format("Total SKUs: %d  |  Total Stock Units: %d  |  Total Inventory Value: Rs. %.2f",
                    products != null ? products.size() : 0, totalStock, totalValuation))
                    .setFontSize(11).setBold().setTextAlignment(TextAlignment.RIGHT);
            document.add(summary);

            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate inventory report: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generates a Purchase / Restock Report PDF.
     */
    public static String generatePurchaseReport(List<PurchaseEntity> purchases) {
        ensureDirectory(REPORTS_DIRECTORY);
        String fileName = REPORTS_DIRECTORY + "/PurchaseReport_" + LocalDate.now() + ".pdf";

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            document.add(new Paragraph("TEECEYLON - PURCHASE / RESTOCK REPORT").setFontSize(18).setBold().setFontColor(THEME_COLOR));
            document.add(new Paragraph("Generated On: " + LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n").setFontSize(6));

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 25, 20, 25})).useAllAvailableWidth();
            String[] headers = {"Purchase ID", "Date", "Supplier", "Invoice No", "Total Cost (Rs.)"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold()).setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            }

            double grandTotal = 0.0;
            if (purchases != null) {
                for (PurchaseEntity p : purchases) {
                    double amt = p.getTotalAmount() != null ? p.getTotalAmount() : 0.0;
                    grandTotal += amt;
                    String supName = p.getSupplier() != null ? p.getSupplier().getSupName() : "-";

                    table.addCell(new Cell().add(new Paragraph(p.getPurchaseId()).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(p.getPurchaseDate() != null ? p.getPurchaseDate().toString() : "").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(supName).setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(p.getSupplierInvoiceNo() != null ? p.getSupplierInvoiceNo() : "-").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", amt)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
                }
            }

            document.add(table);
            document.add(new Paragraph("\n").setFontSize(6));

            Paragraph summary = new Paragraph(String.format("Total Purchases: %d  |  Total Purchase Cost: Rs. %.2f",
                    purchases != null ? purchases.size() : 0, grandTotal))
                    .setFontSize(11).setBold().setTextAlignment(TextAlignment.RIGHT);
            document.add(summary);

            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate purchase report: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generates a Supplier Listing Report PDF.
     */
    public static String generateSupplierReport(List<SupplierEntity> suppliers) {
        ensureDirectory(REPORTS_DIRECTORY);
        String fileName = REPORTS_DIRECTORY + "/SupplierReport_" + LocalDate.now() + ".pdf";

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            document.add(new Paragraph("TEECEYLON - SUPPLIER LISTING").setFontSize(18).setBold().setFontColor(THEME_COLOR));
            document.add(new Paragraph("Generated On: " + LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n").setFontSize(6));

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 25, 20, 20, 20})).useAllAvailableWidth();
            String[] headers = {"Supplier ID", "Name", "Contact", "Email", "Address"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold()).setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            }

            if (suppliers != null) {
                for (SupplierEntity s : suppliers) {
                    table.addCell(new Cell().add(new Paragraph(s.getSupId()).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(s.getSupName() != null ? s.getSupName() : "").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(s.getSupContact() != null ? s.getSupContact() : "").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(s.getSupEmail() != null ? s.getSupEmail() : "").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(s.getSupAddress() != null ? s.getSupAddress() : "").setFontSize(9)));
                }
            }

            document.add(table);
            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate supplier report: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generates an Employee Listing Report PDF.
     */
    public static String generateEmployeeReport(List<EmployeeEntity> employees) {
        ensureDirectory(REPORTS_DIRECTORY);
        String fileName = REPORTS_DIRECTORY + "/EmployeeReport_" + LocalDate.now() + ".pdf";

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(25, 25, 25, 25);

            document.add(new Paragraph("TEECEYLON - EMPLOYEE DIRECTORY").setFontSize(18).setBold().setFontColor(THEME_COLOR));
            document.add(new Paragraph("Generated On: " + LocalDate.now()).setFontSize(10));
            document.add(new Paragraph("\n").setFontSize(6));

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 25, 20, 25})).useAllAvailableWidth();
            String[] headers = {"Emp ID", "Role", "Name", "Contact", "Email"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(9).setBold()).setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            }

            if (employees != null) {
                for (EmployeeEntity emp : employees) {
                    table.addCell(new Cell().add(new Paragraph(emp.getEmpId()).setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(emp.getEmpRole() != null ? emp.getEmpRole() : "").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(emp.getEmpName() != null ? emp.getEmpName() : "").setFontSize(9)));
                    table.addCell(new Cell().add(new Paragraph(emp.getContactNum() != null ? emp.getContactNum() : "").setFontSize(9)).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(emp.getEmail() != null ? emp.getEmail() : "").setFontSize(9)));
                }
            }

            document.add(table);
            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate employee report: " + e.getMessage(), e);
            return null;
        }
    }

    // Legacy fallback methods
    private static boolean generateSimplePdf(String text, String filePath) {
        try {
            ensureDirectory(new File(filePath).getParent());
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                document.add(new Paragraph(text));
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating simple PDF: " + e.getMessage(), e);
            return false;
        }
    }

    public static String createBill(String text) {
        String path = BASE_DIRECTORY + "/LastBill.pdf";
        return generateSimplePdf(text, path) ? path : null;
    }

    public static boolean generateAnnualSalesReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/AnnualSalesReport_" + date.getYear() + ".pdf");
    }

    public static boolean generateMonthlySalesReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/MonthlySalesReport_" + date.getMonth() + "_" + date.getYear() + ".pdf");
    }

    public static boolean generateDailySalesReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/DailySalesReport_" + date + ".pdf");
    }

    public static boolean generateEmployeeReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/EmployeeReport_" + date + ".pdf");
    }

    public static boolean generateProductReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/ProductReport_" + date + ".pdf");
    }

    public static boolean generateSupplierReport(String text, LocalDate date) {
        return generateSimplePdf(text, REPORTS_DIRECTORY + "/SupplierReport_" + date + ".pdf");
    }
}
