package com.babyshop.controller.pos;


import com.babyshop.entity.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

import com.babyshop.model.EmployeeModel;
import com.babyshop.model.InvoiceModel;
import com.babyshop.model.ProductModel;
import com.babyshop.model.SalesModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class InvoiceController implements Initializable {

    @FXML
    private TextField totalAmountField, paidAmountField;
    private double netPrice;
    private ObservableList<Item> items;
    private EmployeeModel employeeModel;
    private ProductModel productModel;
    private SalesModel salesModel;
    private InvoiceModel invoiceModel;
    private Payment payment;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean firstCall = true;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productModel = new ProductModel();
        employeeModel = new EmployeeModel();
        salesModel = new SalesModel();
        invoiceModel = new InvoiceModel();
        totalAmountField.setText(String.valueOf(netPrice));
    }

    public void setData(double netPrice, ObservableList<Item> items, Payment payment) {

        this.netPrice = netPrice;
        this.items = FXCollections.observableArrayList(items);
        this.payment = payment;
    }

    private double getRoundedOffValue(double value) {
        String formattedValue = String.format("%.2f", value);
        return Double.parseDouble(formattedValue);
    }

    @FXML
    public void confirmAction(ActionEvent event) throws Exception {

        if (validateInput()) {
            double paid = Double.parseDouble(paidAmountField.getText().trim());
            double retail = getRoundedOffValue(paid - payment.getSubTotal());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(new Date());

            String invoiceId = formattedDate + "-" + new Timestamp(System.currentTimeMillis()).getTime();

            Invoice invoice = new Invoice(
                    invoiceId,
                    employeeModel.getEmployee(2),
                    payment.getSubTotal(),
                    payment.getPayable(),
                    paid,
                    retail,
                    formattedDate
            );

            invoiceModel.saveInvoice(invoice);

            for (Item i : items) {
                Product p = productModel.getProductByName(i.getItemName());

                // Check if this is the first time updating quantity
                double quantity;
                if (isFirstTime(p)) { // Assume isFirstTime is a method to check the first-time condition
                    quantity = (p.getQuantity() + i.getQuantity()) - i.getQuantity();
                    System.out.println("First time");
                    System.out.println(p.getQuantity() + " " + i.getQuantity() + " " + quantity);
                } else {
                    quantity = p.getQuantity() - i.getQuantity();
                }

                System.out.println(p.getQuantity() + " " + i.getQuantity() + " " + quantity);
                p.setQuantity(quantity);
                productModel.decreaseProduct(p);

                Sale sale = new Sale(
                        invoiceModel.getInvoice(invoiceId),
                        productModel.getProductByName(i.getItemName()),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotal(),
                        formattedDate
                );

                salesModel.saveSale(sale);
            }

            FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Confirm.fxml")));
            ConfirmController controller = new ConfirmController();
            controller.setData(retail, items, invoiceId);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setTitle("Confirm");
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.show();
        }

    }


    private boolean validateInput() {

        String errorMessage = "";

        if (paidAmountField.getText() == null || paidAmountField.getText().length() == 0) {
            errorMessage += "Invalid Input!\n";
        } else if (Double.parseDouble(paidAmountField.getText()) < netPrice) {
            errorMessage += "Insufficient Input!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please input the valid amount");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            paidAmountField.setText("");

            return false;
        }
    }

    private boolean isFirstTime(Product product) {
        if (firstCall) {
            firstCall = false;
            return true;
        }
        return false; // Not the first time
    }

    @FXML
    public void closeAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
