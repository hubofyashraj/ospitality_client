package com.ospitality.client.labtechnician;

import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class testReportController {

    public StackPane printPane;
    @FXML
    private Label hospitalNameTxt;

    @FXML
    private TextField patientNameTxt;

    @FXML
    private TextField testNameTxt;

    @FXML
    private TextField labTechnicianNameTxt;

    @FXML
    private TextField dateTxt;

    @FXML
    private Button printReportBTN;

    @FXML
    private Button backBTN;

    @FXML
    public void initialize(){
        dateTxt.setText(LocalDate.now().toString());
        labTechnicianNameTxt.setText(user.getUserName());

        String[] data = dashboardController.data.split("\\./");


        patientNameTxt.setText(data[3]);
        testNameTxt.setText(data[1]);

    }

    @FXML
    void callBack( ) throws IOException {
        Stage stage = (Stage) backBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callPrintReport( ) throws IOException {
        PrinterJob pj = PrinterJob.createPrinterJob();
        if(pj!=null){
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,0,0,0,0);
            JobSettings jobSettings = pj.getJobSettings();
            jobSettings.setPrintQuality(PrintQuality.HIGH);
            jobSettings.setPageLayout(pageLayout);
            jobSettings.setPrintQuality(PrintQuality.DRAFT);
            pj.showPrintDialog(printPane.getScene().getWindow());
            boolean printed = pj.printPage(printPane);
            if(printed){
                pj.endJob();
            }
        }
        DataOutputStream dout = common.getDout();
        dout.writeUTF("LTD");
        dout.writeUTF(dashboardController.data);
    }

}
