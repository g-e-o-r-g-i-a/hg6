module com.example.hg6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.management;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.junit.jupiter.api;
    requires org.junit.platform.commons;

    opens com.example.laborator6 to javafx.fxml;
    exports com.example.hg6;

    opens Repository to java.management, org.junit.platform.engine, org.junit.platform.commons;
    exports Repository;
}