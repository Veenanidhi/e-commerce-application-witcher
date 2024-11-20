package com.witcher.e_commerce.application.witcher.controller.admin;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.service.dashboard.DashboardService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

@Controller
@RequestMapping("/admin")
public class SalesReportController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel() throws IOException{

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");

        // for header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Total Users");
        header.createCell(1).setCellValue("Today's Sales");
        header.createCell(2).setCellValue("Today's Revenue");
        header.createCell(3).setCellValue("Total Revenue");

//        Integer todaySales = orderRepository.findTodaySalesCount();
        Long totalUsers = ((Number) dashboardService.getTotalUsers()).longValue();
        Double todayRevenue = ((Number) dashboardService.getTodayRevenue()).doubleValue();
        Double totalRevenue = ((Number) dashboardService.getTotalRevenue()).doubleValue();


        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(totalUsers);
//        dataRow.createCell(1).setCellValue(todaySales);
        dataRow.createCell(2).setCellValue(todayRevenue);
        dataRow.createCell(3).setCellValue(totalRevenue);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.xlsx" );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(outputStream.toByteArray());

    }

    @GetMapping("/downloadPdf")
    public ResponseEntity <byte[]> downloadPdf() throws DocumentException, IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        document.add(new Paragraph("Sales Report"));
        document.add(new Paragraph("Total Users: " + dashboardService.getTotalUsers()));
        document.add(new Paragraph("Today's Sales: " + orderRepository.findTodaySalesCount()));
        document.add(new Paragraph("Today's Revenue: ₹" + dashboardService.getTodayRevenue()));
        document.add(new Paragraph("Total Revenue: ₹" + dashboardService.getTotalRevenue()));

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());

    }



}
