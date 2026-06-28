package com.siva.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.siva.dto.TransactionResponseDTO;
import com.siva.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/transactions/download")
@RequiredArgsConstructor
public class DataExportController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<byte[]> downloadTransactions(
            @RequestParam(required = true) String format,
            @RequestParam(required = false) String financialYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String organizationName,
            Authentication authentication) throws Exception {

        if (!"JSON".equalsIgnoreCase(format) && !"CSV".equalsIgnoreCase(format) 
            && !"PDF".equalsIgnoreCase(format) && !"XLSX".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().body("Unsupported format".getBytes());
        }

        String email = authentication.getName();
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(
                email, financialYear, month, type, organizationName);

        byte[] outputData;
        HttpHeaders headers = new HttpHeaders();

        if ("JSON".equalsIgnoreCase(format)) {
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("totalRecords", transactions.size());
            exportData.put("transactions", transactions);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            outputData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportData);

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "transactions.json");
        } else if ("CSV".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("Date,Organization Name,Type,Amount,Tax Amount,Financial Year\n");
            for (TransactionResponseDTO txn : transactions) {
                csv.append(String.format("%s,%s,%s,%.2f,%.2f,%s\n",
                        txn.getTransactionDate(),
                        txn.getOrganizationName() != null ? txn.getOrganizationName().replace(",", " ") : "", // escape commas
                        txn.getType(),
                        txn.getAmount() != null ? txn.getAmount().doubleValue() : 0.0,
                        txn.getTaxAmount() != null ? txn.getTaxAmount().doubleValue() : 0.0,
                        txn.getFinancialYear()
                ));
            }
            outputData = csv.toString().getBytes();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "transactions.csv");
        } else if ("PDF".equalsIgnoreCase(format)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, out);
                document.open();

                Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                fontTitle.setSize(18);
                Paragraph title = new Paragraph("Transactions Report", fontTitle);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(title);
                
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100f);
                table.setSpacingBefore(15);
                
                String[] headersList = {"Date", "Org Name", "Type", "Amount", "Tax Amount", "Fin. Year"};
                for (String headerTitle : headersList) {
                    PdfPCell cell = new PdfPCell(new Phrase(headerTitle));
                    cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                
                for (TransactionResponseDTO txn : transactions) {
                    table.addCell(String.valueOf(txn.getTransactionDate()));
                    table.addCell(txn.getOrganizationName() != null ? txn.getOrganizationName() : "");
                    table.addCell(txn.getType());
                    table.addCell(String.format("%.2f", txn.getAmount() != null ? txn.getAmount().doubleValue() : 0.0));
                    table.addCell(String.format("%.2f", txn.getTaxAmount() != null ? txn.getTaxAmount().doubleValue() : 0.0));
                    table.addCell(txn.getFinancialYear());
                }
                
                document.add(table);
                document.close();
                outputData = out.toByteArray();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "transactions.pdf");
            }
        } else {
            // XLSX
            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet("Transactions");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"Date", "Organization Name", "Type", "Amount", "Tax Amount", "Financial Year"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }
                
                int rowIdx = 1;
                for (TransactionResponseDTO txn : transactions) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(txn.getTransactionDate() != null ? txn.getTransactionDate().toString() : "");
                    row.createCell(1).setCellValue(txn.getOrganizationName() != null ? txn.getOrganizationName() : "");
                    row.createCell(2).setCellValue(txn.getType() != null ? txn.getType() : "");
                    row.createCell(3).setCellValue(txn.getAmount() != null ? txn.getAmount().doubleValue() : 0.0);
                    row.createCell(4).setCellValue(txn.getTaxAmount() != null ? txn.getTaxAmount().doubleValue() : 0.0);
                    row.createCell(5).setCellValue(txn.getFinancialYear() != null ? txn.getFinancialYear() : "");
                }
                
                workbook.write(out);
                outputData = out.toByteArray();
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDispositionFormData("attachment", "transactions.xlsx");
            }
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputData);
    }
}
