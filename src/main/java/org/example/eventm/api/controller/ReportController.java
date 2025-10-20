package org.example.eventm.api.controller;

import org.example.eventm.api.dtos.ReportData;
import org.example.eventm.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ReportData>> getReport() {
        return reportService.generateReportAsync()
                .thenApply(ResponseEntity::ok);
    }
}
