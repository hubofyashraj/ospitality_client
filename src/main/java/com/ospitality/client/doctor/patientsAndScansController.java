package com.ospitality.client.doctor;

import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class patientsAndScansController {

    @FXML
    public static String patientData = "";
    @FXML
    public Button searchBtn;
    @FXML
    public TextField searchPatientField;
    @FXML
    public Label noPatientMatchLbl;
    @FXML
    public StackPane mainpane;
    @FXML
    public StackPane navbarStackPane;
    @FXML
    public ScrollPane recentPatientsSP;
    @FXML
    public HBox recentPatientsHbox;
    @FXML
    public ScrollPane todaysAppointmentsSP;
    @FXML
    public HBox todaysAppointmentsHbox;
    @FXML
    private Button patientsAndScansBTN;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button logOutBTN;
    @FXML
    private Button settingsBTN;
    @FXML
    private Button notificationBTN;

    


    @FXML
    void callDashboard() throws IOException {
        Stage stage = (Stage) dashboardBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callLogOut() throws IOException {

        common.logout(mainpane);
    }

    

    @FXML
    void callPatientsAndScans() throws IOException {
        Stage stage = (Stage) patientsAndScansBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patientsAndScans.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);

    }

    @FXML
    void callSettings() throws IOException {
        Stage stage = (Stage) settingsBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/ospitality/client/settings.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }


    @FXML
    public void initialize() {
        recentPatientsSP.setOnScroll(
                event -> {
                    if(event.getDeltaX()==0 && event.getDeltaY()!=0){
                        recentPatientsSP.setHvalue(recentPatientsSP.getHvalue()-(event.getDeltaY()/ recentPatientsHbox.getWidth()));
                    }
                }
        );

        todaysAppointmentsSP.setOnScroll(
                event -> {
                    if(event.getDeltaX()==0 && event.getDeltaY()!=0){
                        todaysAppointmentsSP.setHvalue(todaysAppointmentsSP.getHvalue()-(event.getDeltaY()/ recentPatientsHbox.getWidth()));
                    }
                }
        );

        recentPatientsHbox.getChildren().clear();
        todaysAppointmentsHbox.getChildren().clear();

        boolean flagSetListener = true;


        try {
            DataOutputStream dout = common.getDout();
            DataInputStream din = common.getDin();
            dout.writeUTF("DPSI");
            dout.writeUTF(user.getDesignation());

            dout.flush();

            boolean c = din.readBoolean();
            if(c){
                String st = din.readUTF();
                while(!st.equals("S")){
                    recentPatientsHbox.getChildren().add(common.getAPane(st.split("\\./")[0],st.split("\\./")[1]));
                    st=din.readUTF();
                }
            }else {
                Label label = new Label("No Patient For Today Yet!");
                label.wrapTextProperty().setValue(true);
                recentPatientsHbox.getChildren().add(label);
            }


            boolean b = din.readBoolean();
            if(b){
                String st = din.readUTF();
                while(!st.equals("S")){
                    todaysAppointmentsHbox.getChildren().add(common.getAPane(st.split("\\./")[0],st.split("\\./")[1]));
                    st=din.readUTF();
                }
            }else{
                Label label = new Label("No Appointments Today Yet!");
                label.wrapTextProperty().set(true);
                todaysAppointmentsHbox.getChildren().add(label);
                flagSetListener=false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(flagSetListener){
            todaysAppointmentsHbox.getChildren().forEach(
                    item -> {
                        AnchorPane pane = (AnchorPane) item;

                        pane.setOnMouseEntered(
                                mouseEvent -> pane.setStyle("-fx-background-color: #403434;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        pane.setOnMouseExited(
                                mouseEvent -> pane.setStyle("-fx-background-color: #3d3d3d;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        pane.setOnMouseClicked(
                                mouseEvent -> {
                                    try {
                                        searchPatientField.setText(((Label) (pane.getChildren().get(1))).getText());
                                        callSearch();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
            );
        }
    }

    public void callSearch() throws IOException {
        DataOutputStream dout = new DataOutputStream(common.getSocket().getOutputStream())/*common.getDout()*/;
        DataInputStream din = new DataInputStream(common.getSocket().getInputStream())/*common.getDin()*/;

        dout.writeUTF("DSP");
        dout.writeUTF(searchPatientField.getText()+"./"+user.getDesignation());

        if(din.readBoolean()){
            patientsAndScansController.patientData = din.readUTF();
            patientController.patID = searchPatientField.getText();
            Stage stage = (Stage) searchBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient.fxml")));
            Scene sc = stage.getScene();
            Scene scene  = new Scene(root,sc.getWidth(),sc.getHeight());
            stage.setScene(scene);
        }else{
            JFXSnackbar snackbar = new JFXSnackbar((Pane) recentPatientsHbox.getParent());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("No Appointment For "+searchPatientField.getText()), Duration.millis(1500)));
        }
    }
}