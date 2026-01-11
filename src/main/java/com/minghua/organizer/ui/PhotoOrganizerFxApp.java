package com.minghua.organizer.ui;

import com.minghua.organizer.service.DuplicateDetectorService;
import com.minghua.organizer.service.ExifService;
import com.minghua.organizer.service.MigrationService;
import com.minghua.organizer.service.PhotoOrganizerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhotoOrganizerFxApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Manual construction of services
        ExifService exif = new ExifService();
        DuplicateDetectorService duplicateDetector = new DuplicateDetectorService();
        PhotoOrganizerService organizer = new PhotoOrganizerService(exif);
        MigrationService migrationService =
                new MigrationService(organizer, duplicateDetector, exif);

        // Manual construction of controller
        MainController controller = new MainController(migrationService);

        // Load FXML and inject controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        loader.setController(controller);

        stage.setTitle("Photo Organizer");
        stage.setScene(new Scene(loader.load(), 1100, 700));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}