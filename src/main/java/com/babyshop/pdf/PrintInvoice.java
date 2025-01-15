package com.babyshop.pdf;

import com.babyshop.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.PrinterJob;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PrintInvoice {

    private final ObservableList<Item> items;

    public PrintInvoice(ObservableList<Item> items) {
        this.items = FXCollections.observableArrayList(items);
    }

    public void generateReport() {
        Rectangle smallPage = new Rectangle(226.77f, 566.93f);
        Document document = new Document(smallPage, 20, 20, 20, 20); // Adjust margins for small size
        String filePath = "Report " + LocalDateTime.now() + ".pdf";
        try (FileOutputStream fs = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, fs);
            document.open();

            addShopHeader(document);
            addItems(document);
            addFooter(document);

            document.close();

            File file = new File(filePath);
            if (Desktop.isDesktopSupported() && file.exists()) {
//                Desktop.getDesktop().open(file);
                System.out.println(file.getAbsolutePath());
                System.out.println("file exists");
            }

            printBill(file);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void printBill(File file) {
//        try {
//            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
//            if (printService == null) {
//                System.out.println("No default print service found.");
//                return;
//            }
//
////            DocPrintJob printJob = printService.createPrintJob();
////            FileInputStream fis = new FileInputStream(file);
////
////            Doc document = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
////
////            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
////            attributes.add(new MediaPrintableArea(0, 0, 58, 160, MediaPrintableArea.MM));
////            attributes.add(OrientationRequested.PORTRAIT);
////
////            printJob.print(document, attributes);
////
////            fis.close();
//
//            // Load the PDF using PDFBox
//            PDDocument document = PDDocument.load(file);
//            PDFRenderer pdfRenderer = new PDFRenderer(document);
//
//            int pageCount = document.getNumberOfPages();
//
//            for (int pageNo = 0; pageNo < pageCount; pageNo++) {
//                // Convert the current page of the PDF to an image
//                Image pdfImage = convertPdfPageToImage(pdfRenderer, pageNo);
//
//                // Display the image in an ImageView
//                ImageView imageView = new ImageView(pdfImage);
//                imageView.setFitWidth(400);
//                imageView.setPreserveRatio(true);
//
////            StackPane root = new StackPane(imageView);
////            Scene scene = new Scene(root, 500, 700);
////            stage.setScene(scene);
////            stage.setTitle("PDF Printer");
////            stage.show();
//
//                // Print the PDF image
//                PrinterJob printerJob = PrinterJob.createPrinterJob();
//                if (printerJob != null /*&& printerJob.showPrintDialog(stage)*/) {
//                    boolean success = printerJob.printPage(imageView);
//                    if (success) {
//                        printerJob.endJob();
//                        System.out.println("Printed successfully!");
//                    } else {
//                        System.out.println("Failed to print.");
//                    }
//                } else {
//                    System.out.println("No printer found or print job cancelled.");
//
//                }
//            }
//
//
//            document.close();
//        } catch (PrintException | IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    private void printBill(File file) {
//        try {
//            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
//            if (printService == null) {
//                System.out.println("No default print service found.");
//                return;
//            }
//
//            // Load the PDF using PDFBox
//            PDDocument document = PDDocument.load(file);
//            PDFRenderer pdfRenderer = new PDFRenderer(document);
//
//            // Get the dimensions of the thermal printer (58mm width is typical)
//            float thermalPaperWidthMM = 58;
//            float dpi = 203; // Typical DPI for thermal printers
//            float thermalPaperWidthPixels = (thermalPaperWidthMM / 25.4f) * dpi;
//
//            // Create a print job
//            DocPrintJob printJob = printService.createPrintJob();
//
//            // Iterate through PDF pages
//            int pageCount = document.getNumberOfPages();
//            for (int pageNo = 0; pageNo < pageCount; pageNo++) {
//                // Convert the current page of the PDF to an image
//                BufferedImage pdfImage = pdfRenderer.renderImageWithDPI(pageNo, dpi);
//
//                // Scale the image to fit the thermal paper width
//                BufferedImage scaledImage = scaleImageToWidth(pdfImage, (int) thermalPaperWidthPixels);
//
//                // Convert the image to a printable format
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(scaledImage, "png", baos);
//                byte[] imageData = baos.toByteArray();
//
//                // Print the image
//                Doc doc = new SimpleDoc(imageData, DocFlavor.BYTE_ARRAY.PNG, null);
//                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//                attributes.add(new MediaPrintableArea(0, 0, thermalPaperWidthMM, scaledImage.getHeight() / dpi * 25.4f, MediaPrintableArea.MM));
//
//                printJob.print(doc, attributes);
//            }
//
//            document.close();
//            System.out.println("Printed successfully!");
//
//        } catch (IOException | PrintException e) {
//            e.printStackTrace();
//        }
//    }

//    private void printBill(File file) {
//        try {
////            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
//            PrintService printService = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PDF, null)[0];
//            if (printService == null) {
//                System.out.println("No default print service found.");
////                return;
//            }
//
//            // Load the PDF using PDFBox
//            PDDocument document = PDDocument.load(file);
//
//            // Create a print job
//            DocPrintJob printJob = printService.createPrintJob();
//
//            // Iterate through PDF pages
//            int pageCount = document.getNumberOfPages();
//            for (int pageNo = 0; pageNo < pageCount; pageNo++) {
//                // Print the PDF page directly
//                Doc doc = new SimpleDoc(new FileInputStream(file), DocFlavor.INPUT_STREAM.PDF, null);
//                printJob.print(doc, new HashPrintRequestAttributeSet());
//            }
//
//            document.close();
//            System.out.println("Printed successfully!");
//
//        } catch (IOException | PrintException e) {
//            e.printStackTrace();
//        }
//    }

    private void printBill(File file) {
        try {
            // Look up the default print service that supports PDF
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PDF, null);
            if (printServices.length == 0) {
                System.out.println("No print service found that supports PDF.");
                return;
            }

            // Load the PDF using PDFBox
            PDDocument document = PDDocument.load(file);

            // Create a print job
            DocPrintJob printJob = printServices[0].createPrintJob();

            // Create a Doc object from the PDF file
            Doc doc = new SimpleDoc(new FileInputStream(file), DocFlavor.INPUT_STREAM.PDF, null);

            // Set up print request attributes
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

            // Add a listener to handle print job events
            printJob.addPrintJobListener(new PrintJobAdapter() {
                @Override
                public void printJobCompleted(PrintJobEvent pje) {
                    System.out.println("Print job completed.");
                }

                @Override
                public void printJobFailed(PrintJobEvent pje) {
                    System.out.println("Print job failed.");
                }
            });

            // Print the document
            printJob.print(doc, pras);

            // Close the document
            document.close();
            System.out.println("Printed successfully!");

        } catch (IOException | PrintException e) {
            e.printStackTrace();
        }
    }
    // Helper method to scale image to the specified width
    private BufferedImage scaleImageToWidth(BufferedImage image, int width) {
        int height = (int) (image.getHeight() * ((float) width / image.getWidth()));
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    private Image convertPdfPageToImage(PDFRenderer pdfRenderer, int pageIndex) throws Exception {
        java.awt.image.BufferedImage awtImage = pdfRenderer.renderImageWithDPI(pageIndex, 300);
        return javafx.embed.swing.SwingFXUtils.toFXImage(awtImage, null);
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