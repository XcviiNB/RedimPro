module com.example.taskreminder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.taskreminder to javafx.fxml;
    exports com.example.taskreminder;
}