package com.tommy.admin.application.service;

import com.tommy.admin.domain.entity.CourseReplica;
import com.tommy.admin.domain.entity.DailyRevenue;
import com.tommy.admin.domain.entity.UserReplica;
import com.tommy.admin.infrastructure.persistence.repository.CourseReplicaRepository;
import com.tommy.admin.infrastructure.persistence.repository.DailyRevenueRepository;
import com.tommy.admin.infrastructure.persistence.repository.UserReplicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final DailyRevenueRepository dailyRevenueRepository;
    private final CourseReplicaRepository courseReplicaRepository;
    private final UserReplicaRepository userReplicaRepository;

    public ByteArrayInputStream generateRevenueReportExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Revenue Report");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Date", "Total Revenue (VND)"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            List<DailyRevenue> revenues = dailyRevenueRepository.findAll();
            int rowIdx = 1;
            for (DailyRevenue revenue : revenues) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(revenue.getDate().toString());
                row.createCell(1).setCellValue(revenue.getTotalRevenue().doubleValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.error("Failed to export revenue report", e);
            throw new RuntimeException("Failed to export revenue report: " + e.getMessage());
        }
    }

    public ByteArrayInputStream generateCourseReportExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Course Report");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Course ID", "Title", "Enrollments", "Completions", "Revenue"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            List<CourseReplica> courses = courseReplicaRepository.findAll();
            int rowIdx = 1;
            for (CourseReplica course : courses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(course.getId().toString());
                row.createCell(1).setCellValue(course.getTitle());
                row.createCell(2).setCellValue(course.getEnrollmentCount());
                row.createCell(3).setCellValue(course.getCompletionCount());
                row.createCell(4).setCellValue(course.getTotalRevenue().doubleValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.error("Failed to export course report", e);
            throw new RuntimeException("Failed to export course report: " + e.getMessage());
        }
    }
}
