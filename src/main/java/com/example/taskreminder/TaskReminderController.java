package com.example.taskreminder;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.awt.Toolkit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javafx.util.StringConverter;

import javafx.geometry.Pos;

public class TaskReminderController {
    @FXML
    private Button addButton;

    @FXML
    private TableView<Tasks> taskTableView;

    @FXML
    private TextField taskTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeTextField;

    @FXML
    private Label timeLabel;

    @FXML
    private Label dateLabel;

    private DateFormat timeFormat;
    private DateFormat dateFormat;
    private DateTimeFormatter dateFormatter;

    public void initialize() {
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                }
                return null;
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        showAlert("Error", null, "Invalid date format. Please use the format dd/MM/yyyy.");
                    }
                }
                return null;
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimeAndDate();
            }
        }, 0, 1000);

        TableColumn<Tasks, String> taskColumn = new TableColumn<>("Task");
        taskColumn.setCellValueFactory(data -> data.getValue().taskProperty());

        TableColumn<Tasks, LocalTime> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(data -> data.getValue().timeProperty());

        TableColumn<Tasks, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());

        timeColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setStyle("-fx-alignment: CENTER;");

        taskTableView.getColumns().addAll(taskColumn, timeColumn, dateColumn);

        addButton.setOnAction(event -> {
            String task = taskTextField.getText();
            String timeText = timeTextField.getText();
            LocalDate date = datePicker.getValue();

            if (task.isEmpty() || timeText.isEmpty() || date == null) {
                Toolkit.getDefaultToolkit().beep();
                showAlert("Error", null, "Invalid credentials.");
            } else if (date.isBefore(LocalDate.now())) {
                Toolkit.getDefaultToolkit().beep();
                showAlert("Error", null, "Tasks cannot be added for past dates.");
                datePicker.setValue(null);
            } else {
                LocalTime time;
                try {
                    time = LocalTime.parse(timeText);
                } catch (DateTimeParseException e) {
                    showAlert("Error", null, "Invalid time format. Please use the format HH:mm:ss.");
                    timeTextField.clear();
                    return;
                }

                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();

                if (date.isEqual(currentDate) && time.isBefore(currentTime)) {
                    Toolkit.getDefaultToolkit().beep();
                    showAlert("Error", null, "You cannot include tasks for earlier times on the current day.");
                    timeTextField.clear();
                    return;
                }

                Tasks newTask = new Tasks(task, time, date);
                taskTableView.getItems().add(newTask);

                taskTextField.clear();
                timeTextField.clear();
                datePicker.setValue(null);
            }
        });


        TableColumn<Tasks, Void> deleteColumn = new TableColumn<>("...");
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("X");
            {
                deleteButton.setOnAction(event -> {
                    Tasks task = getTableView().getItems().get(getIndex());
                    deleteAlert(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        taskTableView.getColumns().add(deleteColumn);
    }

    private void updateTimeAndDate() {
        Date now = new Date();
        Platform.runLater(() -> {
            String timeString = timeFormat.format(now);
            timeLabel.setText(timeString);
            LocalTime currentTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
            for (Tasks task : taskTableView.getItems()) {
                if (task.getTime().equals(currentTime) && task.getDate().equals(LocalDate.now())) {
                    taskAlert("Tast Alert!", task.getTask(), null, task);
                    break;
                }
            }
        });

        Platform.runLater(() -> {
            String date = dateFormat.format(now);
            dateLabel.setText(date);
        });
    }

    private void taskAlert(String title, String header, String content, Tasks task) {
        Platform.runLater(() -> {
            Timer timer = new Timer();
            TimerTask beepTask = new TimerTask() {
                @Override
                public void run() {
                    Toolkit.getDefaultToolkit().beep();
                }
            };
            timer.scheduleAtFixedRate(beepTask, 0, 500);

            Label text = new Label(content);
            text.setStyle("-fx-font-size: 14px;");
            text.setAlignment(Pos.CENTER);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(null);
            alert.getDialogPane().setContent(text);
            alert.getButtonTypes().clear();

            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().add(closeButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == closeButton) {
                    beepTask.cancel();
                    timer.cancel();
                    timer.purge();
                    taskTableView.getItems().remove(task);
                }
            });
        });
    }

    private void deleteAlert(Tasks task) {
        Platform.runLater(() -> {
            Toolkit.getDefaultToolkit().beep();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Removal of task");

            Label content = new Label("Are you sure you want to remove this task?");
            content.setStyle("-fx-font-size: 14px;");
            content.setAlignment(Pos.CENTER);
            alert.setContentText(null);
            alert.getDialogPane().setContent(content);

            alert.getButtonTypes().clear();

            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().addAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    taskTableView.getItems().remove(task);
                }
            });
        });
    }
    private void showAlert(String title, String header, String content) {
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        Label text = new Label(content);
        text.setStyle("-fx-font-size: 14px;");
        text.setAlignment(Pos.CENTER);
        alert.setContentText(null);
        alert.getDialogPane().setContent(text);

        alert.showAndWait();
    }
}

