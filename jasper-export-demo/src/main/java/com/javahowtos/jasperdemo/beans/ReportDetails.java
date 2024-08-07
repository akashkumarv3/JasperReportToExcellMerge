package com.javahowtos.jasperdemo.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ReportDetails {
    private String id;
    private String folderName;
    private List<String> jrxlsReportList;
    private List<String> xlnsReportList;
    private int totalReport;
	private String mergedExcelReportName;

    // No-arguments constructor with auto-generated ID
    public ReportDetails() {
        this.id = UUID.randomUUID().toString(); // Auto-generate ID
        this.jrxlsReportList = new ArrayList<>(); // Initialize the list
        this.xlnsReportList = new ArrayList<>(); // Initialize the list
    }

    // Overloaded constructor for convenience
    public ReportDetails(String folderName, List<String> jrxlsReportList, List<String> xlnsReportList) {
        this(); // Call the no-arg constructor to generate ID
        this.folderName = folderName;
        this.jrxlsReportList = jrxlsReportList;
        this.xlnsReportList = xlnsReportList;
        this.totalReport = jrxlsReportList.size() + xlnsReportList.size(); // Calculate total reports
    }


    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<String> getJrxlsReportList() {
        return jrxlsReportList;
    }

    public void setJrxlsReportList(List<String> jrxlsReportList) {
        this.jrxlsReportList = jrxlsReportList;
        updateTotalReport(); // Update total when list changes
    }

    public List<String> getXlnsReportList() {
        return xlnsReportList;
    }

    public void setXlnsReportList(List<String> xlnsReportList) {
        this.xlnsReportList = xlnsReportList;
        updateTotalReport(); // Update total when list changes
    }

    public int getTotalReport() {
        return totalReport;
    }
    public void setTotalReport(int totalReport) {
		this.totalReport = totalReport;
	}
    
    private void updateTotalReport() {
        this.totalReport = (jrxlsReportList != null ? jrxlsReportList.size() : 0) +
                           (xlnsReportList != null ? xlnsReportList.size() : 0);
    }

    @Override
    public String toString() {
        return "ReportDetails{" +
                "id='" + id + '\'' +
                ", folderName='" + folderName + '\'' +
                ", jrxlsReportList=" + jrxlsReportList +
                ", xlnsReportList=" + xlnsReportList +
                ", totalReport=" + totalReport +
                '}';
    }

	public String getMergedExcelReportName() {
		return mergedExcelReportName;
	}

	public void setMergedExcelReportName(String mergedExcelReportName) {
		this.mergedExcelReportName = mergedExcelReportName;
	}
}
