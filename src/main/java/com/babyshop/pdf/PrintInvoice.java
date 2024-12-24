package com.babyshop.pdf;

import com.babyshop.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintInvoice {

    private final ObservableList<Item> items;

    public PrintInvoice(ObservableList<Item> items) {
        this.items = FXCollections.observableArrayList(items);
    }

    public void generateReport() {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // Set page margins
        String filePath = "Report.pdf";
        try (FileOutputStream fs = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, fs);
            document.open();

            addShopHeader(document);
            addItemTable(document);
            addFooter(document);

            document.close();

            // Open the PDF file in the default viewer
            File file = new File(filePath);
            if (Desktop.isDesktopSupported() && file.exists()) {
                Desktop.getDesktop().open(file);
            }
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace(); // Log or handle the exception as needed
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exceptions during file output stream operations
        }
    }

    private void addShopHeader(Document document) throws DocumentException {
        Font shopFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
        Paragraph shopName = new Paragraph("Baby Shop", shopFont);
        shopName.setAlignment(Element.ALIGN_CENTER);
        document.add(shopName);
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(separator);

        document.add(new Paragraph(" "));
    }

    private void addItemTable(Document document) throws DocumentException {
        PdfPTable table = createItemTable();
        document.add(table);
    }

    private PdfPTable createItemTable() {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addTableHeader(table);
        addTableRows(table);

        return table;
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"Item", "Price", "Quantity", "Total"};
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }
        table.setHeaderRows(1);
    }

    private void addTableRows(PdfPTable table) {
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        for (Item item : items) {
            PdfPCell itemNameCell = new PdfPCell(new Phrase(item.getItemName(), cellFont));
            PdfPCell priceCell = new PdfPCell(new Phrase(String.valueOf(item.getUnitPrice()), cellFont));
            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), cellFont));
            PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(item.getTotal()), cellFont));

            priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(itemNameCell);
            table.addCell(priceCell);
            table.addCell(quantityCell);
            table.addCell(totalCell);
        }
    }

    private void addFooter(Document document) throws DocumentException {
        double grandTotal = items.stream()
                .mapToDouble(Item::getTotal)
                .sum();

        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Thank you for shopping with us!", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        Paragraph totalParagraph = new Paragraph("Grand Total: " + String.format("%.2f", grandTotal), totalFont);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingBefore(10f); // Add spacing before the total
        document.add(totalParagraph);
    }


    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
