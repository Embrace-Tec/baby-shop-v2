package com.babyshop.pdf;

import com.babyshop.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintInvoice {

    private final ObservableList<Item> items;

    public PrintInvoice(ObservableList<Item> items) {
        this.items = FXCollections.observableArrayList(items);
    }

    public void generateReport() {
        Rectangle smallPage = new Rectangle(226.77f, 566.93f);
        Document document = new Document(smallPage, 20, 20, 20, 20); // Adjust margins for small size
        String filePath = "Report.pdf";
        try (FileOutputStream fs = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, fs);
            document.open();

            addShopHeader(document);
            addItems(document);
            addFooter(document);

            document.close();

            File file = new File(filePath);
            if (Desktop.isDesktopSupported() && file.exists()) {
                Desktop.getDesktop().open(file); // Open the file if needed
            }

            // Print the bill
            printBill(file);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printBill(File file) {
        try {
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                System.out.println("No default print service found.");
                return;
            }

            DocPrintJob printJob = printService.createPrintJob();
            FileInputStream fis = new FileInputStream(file);

            Doc document = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);

            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new MediaPrintableArea(0, 0, 58, 160, MediaPrintableArea.MM));
            attributes.add(OrientationRequested.PORTRAIT);

            printJob.print(document, attributes);

            fis.close();
        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addShopHeader(Document document) throws DocumentException {
        Font shopFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Paragraph shopName = new Paragraph("mom & baby", shopFont);
        shopName.setAlignment(Element.ALIGN_CENTER);
        document.add(shopName);

        Font addressFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
        Paragraph address = new Paragraph("mom & baby, Ketethanna, Kahawatta\nContact No: 074 30 30 174", addressFont);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(5f);
        document.add(address);

        shopName.setSpacingAfter(3f);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(separator);
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.DARK_GRAY);
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Paragraph dateTime = new Paragraph("Date & Time: " + currentDateTime, dateFont);

        dateTime.setAlignment(Element.ALIGN_RIGHT);

        dateTime.setSpacingBefore(8f);
        dateTime.setSpacingAfter(8f);

        document.add(dateTime);

        document.add(new Paragraph(" "));

    }

    private void addItems(Document document) throws DocumentException {
        Font itemFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);

        Paragraph header = new Paragraph();
        header.add(new Chunk("Item", boldFont));
        header.add(new Chunk("                      ", itemFont));
        header.add(new Chunk("Price", boldFont));
        header.add(new Chunk("      ", itemFont));
        header.add(new Chunk("Quantity", boldFont));
        header.add(new Chunk("    ", itemFont));
        header.add(new Chunk("Total", boldFont));

        header.setSpacingAfter(3f);
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(header);
        document.add(separator);

        for (Item item : items) {
            Paragraph itemParagraph = new Paragraph();
            itemParagraph.add(new Chunk(item.getItemName(), itemFont));
            itemParagraph.add(new Chunk("                      ", itemFont));
            itemParagraph.add(new Chunk(String.valueOf(item.getUnitPrice()), itemFont));
            itemParagraph.add(new Chunk("      ", itemFont));
            itemParagraph.add(new Chunk(String.valueOf(item.getQuantity()), itemFont));
            itemParagraph.add(new Chunk("    ", itemFont));
            itemParagraph.add(new Chunk(String.valueOf(item.getTotal()), itemFont));
            itemParagraph.setSpacingAfter(5f);
            document.add(itemParagraph);
        }
    }


    private void addFooter(Document document) throws DocumentException {
        double grandTotal = items.stream()
                .mapToDouble(Item::getTotal)
                .sum();

        Font totalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
        Paragraph totalParagraph = new Paragraph("Order Total: " + String.format("%.2f", grandTotal), totalFont);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingAfter(5f);
        document.add(totalParagraph);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.BLACK);
        document.add(new Chunk(separator));

        Font thankYouFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Paragraph thankYouParagraph = new Paragraph("Thank you, come again!", thankYouFont);
        thankYouParagraph.setAlignment(Element.ALIGN_CENTER);
        thankYouParagraph.setSpacingBefore(10f);
        document.add(thankYouParagraph);

        Font contactFont = new Font(Font.FontFamily.HELVETICA, 6, Font.ITALIC, BaseColor.GRAY);
        Paragraph contactParagraph = new Paragraph("Contact: 0769144363", contactFont);
        contactParagraph.setAlignment(Element.ALIGN_CENTER);
        contactParagraph.setSpacingBefore(5f);
        document.add(contactParagraph);

        Font copyrightFont = new Font(Font.FontFamily.HELVETICA, 6, Font.ITALIC, BaseColor.GRAY);
        Paragraph copyrightParagraph = new Paragraph("Copyright (c) 2025 embracetec. All Rights Reserved.", copyrightFont);
        copyrightParagraph.setAlignment(Element.ALIGN_CENTER);
        copyrightParagraph.setSpacingBefore(5f);
        document.add(copyrightParagraph);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}