package com.babyshop.controller.supplier;


import com.babyshop.entity.Supplier;
import com.babyshop.interfaces.SupplierInterface;
import com.babyshop.model.SupplierModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddController implements Initializable, SupplierInterface {

    @FXML
    private TextField supplierField, phoneField;
    @FXML
    private TextArea addressArea;
    @FXML
    private Button saveButton;
    private SupplierModel supplierModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        supplierModel = new SupplierModel();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        supplierField.setText("");
        phoneField.setText("");
        addressArea.setText("");
    }

    @FXML
    public void handleSave(ActionEvent event) {

        if (validateInput()) {

            Supplier supplier = new Supplier(
                    supplierField.getText(),
                    phoneField.getText(),
                    addressArea.getText()
            );

            supplierModel.saveSuplier(supplier);
            SUPPLIERLIST.clear();
            SUPPLIERLIST.addAll(supplierModel.getSuppliers());

            ((Stage) saveButton.getScene().getWindow()).close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successful");
            alert.setHeaderText("Employee Created!");
            alert.setContentText("Employee is created successfully");
            alert.showAndWait();
        }
    }

    private boolean validateInput() {

        String errorMessage = "";

        if (supplierField.getText() == null || supplierField.getText().length() == 0) {
            errorMessage += "No valid first name!\n";
        }

        if (phoneField.getText() == null || phoneField.getText().length() == 0) {
            errorMessage += "No valid phone number!\n";
        }

        if (addressArea.getText() == null || addressArea.getText().length() == 0) {
            errorMessage += "No email address!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }
    }
    
    @FXML
    public void closeAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
