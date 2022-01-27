package com.ospitality.client.doctor;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.ospitality.client.common.din;

public class patientController {

    static String patID="";

    @FXML
    public StackPane mainpane;
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
        JFXDialogLayout content = new JFXDialogLayout();
        Text text = new Text("Select Test From Menu");
        text.setFill(Color.valueOf("#282331"));
        content.setHeading(text);
        ComboBox<String> box = new ComboBox<String>();
        ObservableList<String> list = FXCollections.observableArrayList(
                "BMP (Basic Metabolic Panel)" ,
               "CBC (Complete Blood Count)" ,
               "CMP (Complete Metabolic Panel)" ,
               "ESR (Erythrocyte Sedimentation Rate)" ,
               "Ferritin" ,
               "FT4 (Free Thyroxine)" ,
               "FT3 (Free Triiodothyronine)" ,
               "GC/CHL (Gonorrhea/Chlamydia)" ,
               "Hemoglobin A1C" ,
               "HIV (fourth generation test)" ,
               "Influenza A and B" ,
               "KOH" ,
               "Lipid Panel" ,
               "Manual Differential" ,
               "Mono (Mononucleosis)" ,
               "Rapid Strep Test" ,
               "TSH (Thyroid Stimulating Hormone)" ,
               "Urinalysis" ,
               "Urine Culture" ,
               "Wet Prep" ,
               "Vitamin B12"
        );
        box.setItems(list);
        content.setBody(new StackPane(box));
        Button assign = new Button("ASSIGN");
        content.setActions(assign);
        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.BOTTOM);
        assign.setOnAction(
                actionEvent -> {
                    try {
                        dout.writeUTF("DAL");
                        dout.writeUTF(patID+"./"+box.getValue());
                        JFXSnackbar snackbar = new JFXSnackbar((Pane) backBtn.getParent());
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Lab-test Assigned "+labtestName.getValue()), Duration.millis(2000)));
                        confermPane.setVisible(false);
                        dialog.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
        dialog.show();
    }

    public void callAssignIt( ) throws IOException {

    }

    public void callDontAssign() {

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
