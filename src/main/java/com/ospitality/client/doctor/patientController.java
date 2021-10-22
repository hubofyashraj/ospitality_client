package com.ospitality.client.doctor;

import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.ospitality.client.common.din;

public class patientController {

    static String patID="";

    @FXML
    public Label patientAge;
    @FXML
    public Button editRemarksBtn;
    @FXML
    public Button generatePrescriptionBTN;
    @FXML
    public ComboBox<String> labtestName;
    @FXML
    public Button assignLabtestBTN;

    @FXML
    public Button confermBTN;
    @FXML
    public StackPane confermPane;
    @FXML
    public Label cancel;
    @FXML
    private Button backBtn;
    @FXML
    private TextArea remarks;
    @FXML
    private Label patientName;
    @FXML
    private ImageView gender;

    String pName = "";
    String pGender = "";
    String pRemark = "";
    int pAge = 0;
    final String patData = dashboardController.patientData;

    final DataOutputStream dout = common.getDout();


    @FXML
    public void initialize() throws IOException, URISyntaxException {
        remarks.setWrapText(true);

        String[] arr = patData.split("\\./");

        pName = arr[0];
        pGender = arr[3];
        pRemark = arr[6];
        pAge = Integer.parseInt(arr[1]);
        patientName.setText(pName);
        patientAge.setText(String.valueOf(pAge));
        if(pGender.equals("male")){
            gender.setImage(new Image(Objects.requireNonNull(getClass().getResource("/icons/male.png")).toURI().toString()));
        }else{
            gender.setImage(new Image(Objects.requireNonNull(getClass().getResource("/icons/female.png")).toURI().toString()));
        }
        pRemark = pRemark.replace("no remarks","");
        pRemark = pRemark.replace("Remarks : ","");
        remarks.setText(pRemark);

    }

    @FXML
    void callBack() throws IOException {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }


    @FXML
    void callEditRemarks() throws IOException {
        dout.writeUTF("DER");
        dout.writeUTF(patID+"./"+user.getDesignation()+"./"+remarks.getText()+"./"+user.getUserID()+"./"+
                pAge+"./"+patData.split("\\./")[3]);
        JFXSnackbar snackbar = new JFXSnackbar((Pane) backBtn.getParent());
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Remarks Saved")));
    }

    public void callGeneratePrescription() throws IOException {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("prescription.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    public void callAssignLabtest() {
        confermPane.setVisible(true);
    }

    public void callAssignIt( ) throws IOException {
        dout.writeUTF("DAL");
        dout.writeUTF(patID+"./"+labtestName.getValue());
        confermPane.setVisible(false);
    }

    public void callDontAssign() {
        confermPane.setVisible(false);
        labtestName.setValue(null);
    }

    public void callMarkVisit() throws IOException {
        dout.writeUTF("DMV");
        dout.writeUTF(patID+"./"+user.getUserID());
        DataInputStream din = common.getDin();
        if(din.readBoolean()){
            JFXSnackbar snackbar = new JFXSnackbar((Pane) backBtn.getParent());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Visit Registered Successfully")));
        }
    }
}
