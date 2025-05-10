package com.witcher.e_commerce.application.witcher.controller.admin;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.CouponRepository;
import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.dashboard.DashboardService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin")
public class SalesReportController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private  CouponRepository couponRepository;

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;



    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");

        // Create header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Total Users");
        header.createCell(1).setCellValue("Today's Sales");
        header.createCell(2).setCellValue("Today's Revenue");
        header.createCell(3).setCellValue("Total Revenue");

        // Create data row with summary metrics
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(dashboardService.getTotalUsers().toString());
        dataRow.createCell(1).setCellValue(orderRepository.findTodaySalesCount().toString());
        dataRow.createCell(2).setCellValue("₹" + dashboardService.getTodayRevenue().toString());
        dataRow.createCell(3).setCellValue("₹" + dashboardService.getTotalRevenue().toString());

        // Create orders section
        Row orderHeader = sheet.createRow(3);
        orderHeader.createCell(0).setCellValue("Product");
        orderHeader.createCell(1).setCellValue("Original Price");
        orderHeader.createCell(2).setCellValue("Discount (%)");
        orderHeader.createCell(3).setCellValue("Purchased Price");

        List<Orders> orders = orderRepository.findAll();
        int rowIndex = 4;
        for (Orders order : orders) {
            Row row = sheet.createRow(rowIndex++);
            Product product = order.getProduct();

            if (product != null) {
                String productName = product.getName();
                double originalPrice = product.getPrice();
                double discount = product.getDiscountedPrice();
                double purchasedPrice = originalPrice - (originalPrice * discount / 100);

                row.createCell(0).setCellValue(productName);
                row.createCell(1).setCellValue("₹" + originalPrice);
                row.createCell(2).setCellValue(discount + "%");
                row.createCell(3).setCellValue("₹" + purchasedPrice);
            } else {
                row.createCell(0).setCellValue("Unknown Product");
                row.createCell(1).setCellValue("-");
                row.createCell(2).setCellValue("-");
                row.createCell(3).setCellValue("-");
            }
        }

        // Create offers section with error handling
        rowIndex += 2; // Add some space
        Row offersHeader = sheet.createRow(rowIndex++);
        offersHeader.createCell(0).setCellValue("Active Offers");

        try {
            // Product offers
            List<ProductOffer> activeProductOffers = productOfferRepository.findAll().stream()
                    .filter(offer -> offer.isActive() && LocalDate.now().isBefore(offer.getExpiryDate()))
                    .collect(Collectors.toList());

            Row productOfferRow = sheet.createRow(rowIndex++);
            productOfferRow.createCell(0).setCellValue("Product Offers:");

            String productOffersInfo = activeProductOffers.isEmpty() ?
                    "No active product offers" :
                    activeProductOffers.stream()
                            .map(offer -> offer.getProductOfferName() + " (" + offer.getDiscountPercentage() + "% off)")
                            .collect(Collectors.joining(", "));

            productOfferRow.createCell(1).setCellValue(productOffersInfo);
        } catch (Exception e) {
            Row errorRow = sheet.createRow(rowIndex++);
            errorRow.createCell(0).setCellValue("Error retrieving product offers");
        }

        try {
            // Category offers
            List<CategoryOffer> activeCategoryOffers = categoryOfferRepository.findAll().stream()
                    .filter(offer -> offer.isActive() && LocalDate.now().isBefore(offer.getExpiryDate()))
                    .collect(Collectors.toList());

            Row categoryOfferRow = sheet.createRow(rowIndex++);
            categoryOfferRow.createCell(0).setCellValue("Category Offers:");

            String categoryOffersInfo = activeCategoryOffers.isEmpty() ?
                    "No active category offers" :
                    activeCategoryOffers.stream()
                            .map(offer -> offer.getCategoryOfferName() + " (" + offer.getDiscountPercentage() + "% off)")
                            .collect(Collectors.joining(", "));

            categoryOfferRow.createCell(1).setCellValue(categoryOffersInfo);
        } catch (Exception e) {
            Row errorRow = sheet.createRow(rowIndex++);
            errorRow.createCell(0).setCellValue("Error retrieving category offers");
        }

        try {
            // Coupons
            List<Coupon> activeCoupons = couponRepository.findAll().stream()
                    .filter(Coupon::isActive)
                    .collect(Collectors.toList());

            Row couponRow = sheet.createRow(rowIndex++);
            couponRow.createCell(0).setCellValue("Active Coupons:");

            String couponsInfo = activeCoupons.isEmpty() ?
                    "No active coupons" :
                    activeCoupons.stream()
                            .map(coupon -> coupon.getCouponName() + " (₹" + coupon.getAmount() + " off)")
                            .collect(Collectors.joining(", "));

            couponRow.createCell(1).setCellValue(couponsInfo);
        } catch (Exception e) {
            Row errorRow = sheet.createRow(rowIndex++);
            errorRow.createCell(0).setCellValue("Error retrieving coupons");
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(outputStream.toByteArray());
    }




    @GetMapping("/downloadPdf")
    public ResponseEntity<byte[]> downloadPdf() throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        document.add(new Paragraph("Sales Report"));
        document.add(new Paragraph("Total Users: " + dashboardService.getTotalUsers()));
        document.add(new Paragraph("Today's Sales: " + orderRepository.findTodaySalesCount()));
        document.add(new Paragraph("Today's Revenue: ₹" + dashboardService.getTodayRevenue()));
        document.add(new Paragraph("Total Revenue: ₹" + dashboardService.getTotalRevenue()));
        document.add(Chunk.NEWLINE);

        // Table headers
        PdfPTable table = new PdfPTable(4); // 4 columns: Product, Original Price, Discount, Final Price
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Stream.of("Product", "Original Price", "Discount (%)", "Purchased Price").forEach(header -> {
            PdfPCell headerCell = new PdfPCell(new Phrase(header));
            headerCell.setBackgroundColor(new BaseColor(230, 230, 250)); // light lavender
            headerCell.setPadding(5);
            table.addCell(headerCell);
        });

        List<Orders> orders = orderRepository.findAll();
        for (Orders order : orders) {
            Product product = order.getProduct();

            if (product != null) {
                String productName = product.getName();
                double originalPrice = product.getPrice();
                double discount = product.getDiscountedPrice();
                double purchasedPrice = originalPrice - (originalPrice * discount / 100);

                table.addCell(productName);
                table.addCell("₹" + originalPrice);
                table.addCell(discount + "%");
                table.addCell("₹" + purchasedPrice);
            } else {
                // null-safe fallback
                table.addCell("Unknown Product");
                table.addCell("-");
                table.addCell("-");
                table.addCell("-");
            }
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        try {
            List<ProductOffer> activeProductOffers = productOfferRepository.findAll().stream()
                    .filter(offer -> offer.isActive() && LocalDate.now().isBefore(offer.getExpiryDate()))
                    .collect(Collectors.toList());

            document.add(new Paragraph("Active Product Discounts/Offers:"));
            if (activeProductOffers.isEmpty()) {
                document.add(new Paragraph("No active product offers"));
            } else {
                for (ProductOffer offer : activeProductOffers) {
                    document.add(new Paragraph("• " + offer.getProductOfferName() + " (" +
                            offer.getDiscountPercentage() + "% off)"));
                }
            }
        } catch (Exception e) {
            document.add(new Paragraph("Error retrieving product offers: " + e.getMessage()));
        }

        document.add(Chunk.NEWLINE);

        try {
            List<CategoryOffer> activeCategoryOffers = categoryOfferRepository.findAll().stream()
                    .filter(offer -> offer.isActive() && LocalDate.now().isBefore(offer.getExpiryDate()))
                    .collect(Collectors.toList());

            document.add(new Paragraph("Active Category Discounts/Offers:"));
            if (activeCategoryOffers.isEmpty()) {
                document.add(new Paragraph("No active category offers"));
            } else {
                for (CategoryOffer offer : activeCategoryOffers) {
                    document.add(new Paragraph("• " + offer.getCategoryOfferName() + " (" +
                            offer.getDiscountPercentage() + "% off)"));
                }
            }
        } catch (Exception e) {
            document.add(new Paragraph("Error retrieving category offers: " + e.getMessage()));
        }

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());
    }






}