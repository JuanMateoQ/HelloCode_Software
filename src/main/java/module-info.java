module org.epn.presentacion {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.epn.presentacion to javafx.fxml;
    exports org.epn.presentacion;
}