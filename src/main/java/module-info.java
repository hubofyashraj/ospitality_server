module com.ospitality.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.java;


    opens com.ospitality.server to javafx.fxml;
    exports com.ospitality.server;
}