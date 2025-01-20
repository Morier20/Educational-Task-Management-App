module org.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;


    opens org.example.demo2 to javafx.fxml;
    exports org.example.demo2;
}