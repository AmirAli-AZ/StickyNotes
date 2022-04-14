module com.amirali.stickynotes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kotlin.stdlib;

    opens com.amirali.stickynotes to javafx.fxml;
    exports com.amirali.stickynotes;
}