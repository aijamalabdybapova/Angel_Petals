package com.flowershop.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ExportUtil {

    private final ObjectMapper objectMapper;

    public ExportUtil() {
        this.objectMapper = new ObjectMapper();
    }

    public InputStream exportToCsv(List<Map<String, Object>> data, String[] headers) throws IOException {
        if (data == null || data.isEmpty()) {
            return new ByteArrayInputStream("No data available".getBytes());
        }

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();

        // Добавляем заголовки
        for (String header : headers) {
            schemaBuilder.addColumn(header);
        }

        CsvSchema schema = schemaBuilder.build().withHeader();

        // Конвертируем данные в CSV
        String csv = csvMapper.writer(schema).writeValueAsString(data);
        return new ByteArrayInputStream(csv.getBytes());
    }

    public InputStream exportUsersToCsv(List<Map<String, Object>> users) throws IOException {
        String[] headers = {"ID", "Username", "Email", "First Name", "Last Name", "Phone", "Registration Date", "Total Orders", "Total Spent"};
        return exportToCsv(users, headers);
    }

    public InputStream exportSalesToCsv(List<Map<String, Object>> sales) throws IOException {
        String[] headers = {"Order Number", "Order Date", "Customer", "Total Amount", "Status", "Items Count", "Bouquet Names"};
        return exportToCsv(sales, headers);
    }

    public InputStream exportBouquetStatsToCsv(List<Map<String, Object>> stats) throws IOException {
        String[] headers = {"Bouquet ID", "Name", "Price", "Category", "In Stock", "Stock Quantity", "Average Rating", "Reviews Count", "Times Ordered", "Total Ordered Quantity"};
        return exportToCsv(stats, headers);
    }
}