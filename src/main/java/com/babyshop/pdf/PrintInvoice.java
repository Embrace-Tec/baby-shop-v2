package com.babyshop.pdf;

import com.babyshop.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintInvoice {

    private final ObservableList<Item> items;
    private final String barcode;

    public PrintInvoice(ObservableList<Item> items, String barcode) {
        this.items = FXCollections.observableArrayList(items);
        this.barcode = barcode;
    }

    public void generateReport() {
        Document document = new Document();
        try (FileOutputStream fs = new FileOutputStream("Report.pdf")) {
            PdfWriter writer = PdfWriter.getInstance(document, fs);
            document.open();

            addProductIDParagraph(document);
            addBarcode(writer, document);
            addItemTable(document);

            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace(); // Log or handle the exception as needed
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exceptions during file output stream operations
        }
    }

    private void addProductIDParagraph(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph("Product ID");
        document.add(paragraph);
        addEmptyLine(paragraph, 5); // Add 5 empty lines for spacing
    }

    private void addBarcode(PdfWriter writer, Document document) throws DocumentException {
        PdfContentByte cb = writer.getDirectContent();
        BarcodeEAN barcodeEAN = new BarcodeEAN();
        barcodeEAN.setCodeType(BarcodeEAN.EAN13); // Set barcode type (EAN13)
        barcodeEAN.setCode(barcode); // Set the barcode value
        document.add(barcodeEAN.createImageWithBarcode(cb, BaseColor.BLACK, BaseColor.DARK_GRAY));
        addEmptyLine(new Paragraph(), 5); // Add space after barcode
    }

    private void addItemTable(Document document) throws DocumentException {
        PdfPTable table = createItemTable();
        document.add(table);
    }

    private PdfPTable createItemTable() {
        PdfPTable table = new PdfPTable(4); // Create a table with 4 columns
        addTableHeader(table);
        addTableRows(table);

        return table;
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"Item", "Price", "Quantity", "Total"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        table.setHeaderRows(1); // Set the first row as the header
    }

    private void addTableRows(PdfPTable table) {
        for (Item item : items) {
            table.addCell(item.getItemName());
            table.addCell(String.valueOf(item.getUnitPrice()));
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getTotal()));
        }
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
