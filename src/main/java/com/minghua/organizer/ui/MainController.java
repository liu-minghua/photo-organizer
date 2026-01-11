package com.minghua.organizer.ui;

import com.minghua.organizer.record.MigrationSummary;
import com.minghua.organizer.service.MigrationListener;
import com.minghua.organizer.service.MigrationService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class MainController {

    @FXML
    public StackPane contentPane;

    private final MigrationService migrationService;

    // Shared state
    private final AtomicReference<Path> sourceDir = new AtomicReference<>();
    private final AtomicReference<Path> targetDir = new AtomicReference<>();
    private MigrationSummary lastSummary;

    // UI pieces
    private TextArea logArea;
    private Label lblSource;
    private Label lblTarget;
    private ProgressBar progressBar;
    private Label lblFolders;
    private Label lblFiles;
    private Label lblDuplicates;
    private Label lblUnreadable;
    private Label lblMoved;

    public MainController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @FXML
    public void initialize() {
        showHome(null);
    }

    // ------------------------------------------------------------
    // Navigation Screens
    // ------------------------------------------------------------

    public void showHome(ActionEvent event) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Welcome to Photo Organizer");
        Label desc = new Label("Organize and migrate your photos safely.\nSelect a source and target, then start migration.");

        Button btnSelectSource = new Button("Select Source Folder");
        Button btnSelectTarget = new Button("Select Target Folder");
        Button btnGoMigration = new Button("Go to Migration");

        btnSelectSource.setOnAction(e -> chooseFolder(true));
        btnSelectTarget.setOnAction(e -> chooseFolder(false));
        btnGoMigration.setOnAction(e -> showMigration(null));

        root.getChildren().addAll(title, desc, btnSelectSource, btnSelectTarget, btnGoMigration);
        setContent(root);
    }

    public void showMigration(ActionEvent event) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        HBox pathRow = new HBox(10);
        Button btnSource = new Button("Select Source");
        Button btnTarget = new Button("Select Target");
        lblSource = new Label("Source: " + orPlaceholder(sourceDir.get()));
        lblTarget = new Label("Target: " + orPlaceholder(targetDir.get()));

        btnSource.setOnAction(e -> chooseFolder(true));
        btnTarget.setOnAction(e -> chooseFolder(false));

        VBox pathLabels = new VBox(5, lblSource, lblTarget);
        pathRow.getChildren().addAll(btnSource, btnTarget, pathLabels);

        HBox counters = new HBox(15);
        lblFolders    = new Label("Folders: 0");
        lblFiles      = new Label("Files: 0");
        lblDuplicates = new Label("Duplicates: 0");
        lblUnreadable = new Label("Unreadable: 0");
        lblMoved      = new Label("Moved: 0");
        counters.getChildren().addAll(lblFolders, lblFiles, lblDuplicates, lblUnreadable, lblMoved);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);

        Button btnStart = new Button("Start Migration");
        btnStart.setOnAction(e -> startMigration());

        VBox logBox = new VBox(5);
        Label logTitle = new Label("Log");
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(false);
        logArea.setPrefRowCount(15);
        logBox.getChildren().addAll(logTitle, logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        root.getChildren().addAll(pathRow, counters, progressBar, btnStart, logBox);
        setContent(root);
    }

    public void showLogs(ActionEvent event) {
        if (logArea == null) {
            showMigration(null);
        } else {
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));
            Label title = new Label("Logs");
            root.getChildren().addAll(title, logArea);
            VBox.setVgrow(logArea, Priority.ALWAYS);
            setContent(root);
        }
    }

    public void showSummary(ActionEvent event) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Last Migration Summary");

        if (lastSummary == null) {
            root.getChildren().addAll(title, new Label("No migration has been run yet."));
        } else {
            root.getChildren().addAll(title,
                    new Label("Folders processed: " + lastSummary.foldersProcessed()),
                    new Label("Files processed: " + lastSummary.filesProcessed()),
                    new Label("Duplicates skipped: " + lastSummary.duplicatesFound()),
                    new Label("Unreadable: " + lastSummary.unreadableFiles()),
                    new Label("Files moved: " + lastSummary.filesMoved()),
                    new Label("Duration (seconds): " + lastSummary.durationSeconds())
            );
        }

        setContent(root);
    }

    public void showSettings(ActionEvent event) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Settings"),
                new Label("Theme: Light (default) – dark mode toggle can be added here"),
                new Label("Log level, defaults, etc. can also go here.")
        );
        setContent(root);
    }

    public void showAbout(ActionEvent event) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("About"),
                new Label("Photo Organizer"),
                new Label("Version 1.0"),
                new Label("Author: Minghua")
        );
        setContent(root);
    }

    // ------------------------------------------------------------
    // Folder Selection
    // ------------------------------------------------------------

    private void chooseFolder(boolean source) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(source ? "Select Source Folder" : "Select Target Folder");
        java.io.File dir = chooser.showDialog(contentPane.getScene().getWindow());
        if (dir != null) {
            if (source) {
                sourceDir.set(dir.toPath());
                if (lblSource != null) lblSource.setText("Source: " + dir.toPath());
            } else {
                targetDir.set(dir.toPath());
                if (lblTarget != null) lblTarget.setText("Target: " + dir.toPath());
            }
        }
    }

    // ------------------------------------------------------------
    // Migration Logic (Option 1 — UI handles empty folder BEFORE thread)
    // ------------------------------------------------------------

    private void startMigration() {
        Path src = sourceDir.get();
        Path tgt = targetDir.get();

        if (src == null || tgt == null) {
            appendLog("Please select both source and target folders before starting.");
            return;
        }

        // 1. Check empty folder BEFORE starting the Task
        if (migrationService.isEmptyFolder(src)) {
            boolean confirmed = showEmptyFolderConfirmation(src);
            if (confirmed) {
                appendLog("Deleting empty folder...");
                MigrationSummary summary =
                        migrationService.deleteEmptySourceAndBuildSummary(src, new UiMigrationListener());
                lastSummary = summary;
                appendLog("Empty folder deleted. Please select another non-empty folder.");
            } else {
                appendLog("Empty folder not deleted. Please select another folder.");
            }
            return;
        }

        // 2. Normal migration flow
        appendLog("Starting migration...");
        if (progressBar != null) {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                migrationService.run(src, tgt, new UiMigrationListener());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (progressBar != null) progressBar.setProgress(1.0);
        });
        task.setOnFailed(e -> {
            if (progressBar != null) progressBar.setProgress(0);
            appendLog("Migration failed: " + task.getException());
        });

        new Thread(task, "migration-thread").start();
    }

    private boolean showEmptyFolderConfirmation(Path folder) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Empty Folder");
        alert.setHeaderText("The selected source folder is empty.");
        alert.setContentText("Delete this folder and choose another one?");

        var result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    private String orPlaceholder(Path path) {
        return path == null ? "<not selected>" : path.toString();
    }

    private void appendLog(String msg) {
        Platform.runLater(() -> {
            if (logArea != null) {
                logArea.appendText(msg + System.lineSeparator());
            }
        });
    }

    // ------------------------------------------------------------
    // Listener Implementation
    // ------------------------------------------------------------

    private class UiMigrationListener implements MigrationListener {

        @Override
        public void onStart(Path source, Path target) {
            appendLog("Migration started.");
        }

        @Override
        public void onFolder(Path folder) {
            appendLog("Folder: " + folder);
        }

        @Override
        public void onFile(Path file) {
            appendLog("File: " + file);
        }

        @Override
        public void onDuplicate(Path file) {
            appendLog("Duplicate skipped: " + file);
        }

        @Override
        public void onUnreadable(Path file, String reason) {
            appendLog("Unreadable: " + file + " (" + reason + ")");
        }

        @Override
        public void onMoved(Path source, Path target) {
            appendLog("Moved: " + source + " -> " + target);
        }

        @Override
        public void onError(Path file, String message) {
            appendLog("Error: " + file + " (" + message + ")");
        }

        @Override
        public void onProgressUpdate(
                int foldersProcessed,
                int filesProcessed,
                int duplicatesFound,
                int unreadableFiles,
                int filesMoved
        ) {
            Platform.runLater(() -> {
                if (lblFolders != null)    lblFolders.setText("Folders: " + foldersProcessed);
                if (lblFiles != null)      lblFiles.setText("Files: " + filesProcessed);
                if (lblDuplicates != null) lblDuplicates.setText("Duplicates: " + duplicatesFound);
                if (lblUnreadable != null) lblUnreadable.setText("Unreadable: " + unreadableFiles);
                if (lblMoved != null)      lblMoved.setText("Moved: " + filesMoved);
            });
        }

        @Override
        public void onFinished(MigrationSummary summary) {
            lastSummary = summary;
            appendLog("Migration finished.");
            appendLog("Folders: " + summary.foldersProcessed());
            appendLog("Files: " + summary.filesProcessed());
            appendLog("Duplicates: " + summary.duplicatesFound());
            appendLog("Unreadable: " + summary.unreadableFiles());
            appendLog("Moved: " + summary.filesMoved());
            appendLog("Duration (s): " + summary.durationSeconds());
        }

        // New trio (required)
        @Override
        public void onInfo(String message) {
            appendLog(message);
        }

        @Override
        public void onError(String message) {
            appendLog("ERROR: " + message);
        }

        @Override
        public void onComplete(MigrationSummary summary) {
            lastSummary = summary;
            appendLog("Summary complete.");
        }
    }
}