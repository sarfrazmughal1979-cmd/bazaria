package com.platform.analytics.application.service;

import com.platform.analytics.domain.repository.SalesMetricRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportGenerationService {

    private final SalesMetricRepository salesMetricRepository;

    public byte[] generateSalesReportExcel(LocalDate startDate, LocalDate endDate) {
        var metrics = salesMetricRepository.findByVendorIdAndMetricDateBetweenOrderByMetricDateAsc(
            null, startDate, endDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Revenue");
        header.createCell(2).setCellValue("Orders");
        header.createCell(3).setCellValue("Commission");
        header.createCell(4).setCellValue("Items Sold");

        int rowNum = 1;
        for (var metric : metrics) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(metric.getMetricDate().toString());
            row.createCell(1).setCellValue(metric.getTotalRevenue().doubleValue());
            row.createCell(2).setCellValue(metric.getTotalOrders());
            row.createCell(3).setCellValue(metric.getTotalCommission().doubleValue());
            row.createCell(4).setCellValue(metric.getItemsSold());
        }

        for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
        return out.toByteArray();
    }
}