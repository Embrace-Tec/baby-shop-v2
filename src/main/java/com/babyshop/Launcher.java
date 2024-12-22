package com.babyshop;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {

    // Offsets for dragging the window
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize Hibernate
        if (HibernateUtil.setEntityManagerFactory()) {
            System.out.println("Hibernate Initialized Successfully!");
        } else {
            System.err.println("Failed to initialize Hibernate.");
        }

        // Load the FXML file for the login screen
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

        // Handle dragging the window
        root.setOnMousePressed((MouseEvent event) -> {
            // Capture initial mouse position when pressed
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            // Move the window with the mouse drag
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // Create the scene and set it on the stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Inventory :: Version 1.0");

        // Set the application icon
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

        // Remove window decoration
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.show();

        // Shutdown Hibernate when the application exits
        primaryStage.setOnCloseRequest(event -> {
            HibernateUtil.shutdown();
        });
    }
}
