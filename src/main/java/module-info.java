module com.ospitality.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.desktop;
    requires com.jfoenix;
    exports com.ospitality.client;
    opens com.ospitality.client to javafx.fxml;
    exports com.ospitality.client.doctor;
    opens com.ospitality.client.doctor to javafx.fxml;
    exports com.ospitality.client.admin;
    opens com.ospitality.client.admin to javafx.fxml;
    exports com.ospitality.client.receptionist;
    opens com.ospitality.client.receptionist to javafx.fxml;
    exports com.ospitality.client.labtechnician;
    opens com.ospitality.client.labtechnician to javafx.fxml;
    exports com.ospitality.client.medicalstorekeeper;
    opens com.ospitality.client.medicalstorekeeper to javafx.fxml;
}