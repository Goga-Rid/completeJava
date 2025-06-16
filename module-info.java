module com.example.demoexam {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.demoexam to javafx.fxml;
    exports com.example.demoexam;
}