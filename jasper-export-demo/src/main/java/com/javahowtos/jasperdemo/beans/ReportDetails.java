package com.javahowtos.jasperdemo.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportDetails {
    private String id;
    private String folderName;
    private List<JrlxsReportBean> jrxlsReportList;
    private int totalReport;
	private String mergedExcelReportName;

    // No-arguments constructor with auto-generated ID
    public ReportDetails() {
        this.id = UUID.randomUUID().toString(); // Auto-generate ID
        this.jrxlsReportList = new ArrayList<>(); // Initialize the list
    }

    // Overloaded constructor for convenience
    public ReportDetails(String folderName, List<JrlxsReportBean> jrxlsReportList) {
        this(); // Call the no-arg constructor to generate ID
        this.folderName = folderName;
        this.jrxlsReportList = jrxlsReportList;
        this.totalReport = jrxlsReportList.size() + jrxlsReportList.size(); // Calculate total reports
    }


  
}
