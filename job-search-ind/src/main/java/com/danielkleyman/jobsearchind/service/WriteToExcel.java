//package com.danielkleyman.jobsearchind.service;
//
//import org.apache.poi.common.usermodel.HyperlinkType;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Map;
//
//public class WriteToExcel {
//    public static final String FILE_PATH = "C:\\Users\\Daniel\\Desktop\\CV\\Positions.xlsx";
//    private static Workbook workbook = null;
//    private static Sheet sheet = null;
//
//    public static void writeToExcel(Map<String, List<String>> jobDetails, String websiteName) {
//        //      saveUnfilteredMapToExcel(unfilteredJobDetails);
//        //     Map<String, List<String>> jobDetails = filterMap(unfilteredJobDetails);
//        fileOperations();
//        writingToFile(jobDetails, websiteName);
//
//    }
//
//    private static void writingToFile(Map<String, List<String>> jobDetails, String websiteName) {
//        // Determine the row index to start writing new data
//        int rowIndex = sheet.getLastRowNum() + 2;
//
//        for (Map.Entry<String, List<String>> entry : jobDetails.entrySet()) {
//            String url = entry.getKey();
//            List<String> details = entry.getValue();
//
//            // Ensure that the details list has the required number of elements
//            if (details.size() >= 4) {
//                Row row = sheet.createRow(rowIndex++);
//                row.createCell(0).setCellValue(""); // Blank column
//                row.createCell(1).setCellValue(getCurrentDate());
//                row.createCell(2).setCellValue(websiteName);
//                row.createCell(3).setCellValue(details.get(0)); // Title
//                row.createCell(4).setCellValue(details.get(2)); // Company
//                row.createCell(5).setCellValue(details.get(3)); // City
//                // Create hyperlink for the URL from entry.getValue()
//                Cell linkCell = row.createCell(6);
//                Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
//                hyperlink.setAddress(url);
//                linkCell.setHyperlink(hyperlink);
//                linkCell.setCellValue(url);
//                row.createCell(7).setCellValue(details.get(1)); // Description
//                row.createCell(8).setCellValue(" ");
//            } else {
//                System.err.println("Insufficient data for URL: " + url);
//            }
//        }
//        writeChangesToFile();
//
//    }
//
//    private static void writeChangesToFile() {
//        // Write changes to the file
//        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
//            workbook.write(fileOut);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                workbook.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void fileOperations() {
//        // Check if the file exists
//        if (Files.exists(Paths.get(FILE_PATH))) {
//            // File exists, so read it
//            try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
//                workbook = new XSSFWorkbook(fis);
//                sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet
//            } catch (IOException e) {
//                e.printStackTrace();
//                return; // Exit if there's an error reading the existing file
//            }
//        } else {
//            createFile();
//
//        }
//    }
//
//    private static void createFile() {
//        // File does not exist, so create a new one
//        workbook = new XSSFWorkbook();
//        sheet = workbook.createSheet("Positions");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue(""); // Blank column
//        headerRow.createCell(1).setCellValue("Date");
//        headerRow.createCell(2).setCellValue("Source");
//        headerRow.createCell(3).setCellValue("Position");
//        headerRow.createCell(4).setCellValue("Company   ");
//        headerRow.createCell(5).setCellValue("Location  ");
//        headerRow.createCell(6).setCellValue("Link");
//        headerRow.createCell(7).setCellValue("Description");
//
//        // Set column widths
//        sheet.setColumnWidth(0, 1000);
//        sheet.setColumnWidth(3, 12000); // Index 3 is the 4th column (Title)
//        sheet.setColumnWidth(4, 10000); // Index 4 is the 5th column (Company)
//        sheet.setColumnWidth(5, 10000); // Index 5 is the 6th column (City)
//        sheet.setColumnWidth(6, 10000); // Index 6 is the 7th column (URL)
//        sheet.setColumnWidth(7, 20000); // Index 7 is the 8th column (Description)
//    }
//
//    public static String getCurrentDate() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//        return LocalDate.now().format(formatter);
//    }
//
//}
//
