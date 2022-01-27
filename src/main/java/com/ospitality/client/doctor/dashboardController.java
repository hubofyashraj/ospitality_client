package com.ospitality.client.doctor;

import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class dashboardController {

    public static String patientData="";
    public Label todaysAppointmentsTxt;
    public Label todaysRemainingAppsTxt;
    public ImageView userImg;
    public Label userNameTxt;
    public Label designationTxt;
    public Label emailTxt;
    public Label phnoTxt;
    public Label joiningDateTxt;
    public Label roleTxt;
    public StackPane navbarStackPane;
    public StackPane mainpane;
    public TilePane appointmentsPane;
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

    public void updateUserPhoto() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Photo");
        File inputFile = chooser.showOpenDialog(null);
        if(inputFile!=null){
            common.updateDP(inputFile,userImg);
        }
    }

    public void removeUserPhoto() throws URISyntaxException, IOException {
        userImg.setImage(new Image(Objects.requireNonNull(getClass().getResource("/icons/user.png")).toURI().toString()));
        common.removeUserDp();
    }

    @FXML
    void initialize() throws IOException, URISyntaxException {
        userNameTxt.setText(user.getUserName());
        roleTxt.setText(user.getRole());
        designationTxt.setText(user.getDesignation());
        emailTxt.setText(user.getWorkEmail());
        phnoTxt.setText(user.getPhNo());
        joiningDateTxt.setText(user.getJoiningDate());

        DataOutputStream dout = common.getDout();
        DataInputStream din = common.getDin();

        dout.writeUTF("DDI");
        dout.writeUTF(designationTxt.getText());

        if(din.readBoolean()){
            String str = din.readUTF();
            todaysAppointmentsTxt.setText(str.split("\\./")[0]);
            todaysRemainingAppsTxt.setText(str.split("\\./")[1]);
        }

        ContextMenu contextMenu = new ContextMenu();
        MenuItem updateDP = new MenuItem("Update Profile");
        MenuItem remove = new MenuItem("Remove");
        contextMenu.getItems().addAll(updateDP,remove);
        updateDP.setOnAction(
                event -> {
                    try {
                        updateUserPhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        remove.setOnAction(
                event -> {
                    try {
                        removeUserPhoto();
                    } catch (URISyntaxException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        userImg.setOnContextMenuRequested(
                event -> {
                    if(contextMenu.isShowing()){
                        contextMenu.hide();
                    }else{
                        contextMenu.show(userImg,event.getScreenX(),event.getScreenY());
                    }
                }
        );

        common.loadDP(userImg);

        dout.writeUTF("DPSI");
        dout.writeUTF(user.getDesignation());

        dout.flush();

        boolean c = din.readBoolean();
        if(c){
            String st = din.readUTF();
            while(!st.equals("S")){
                String[] str = st.split("\\./");
                appointmentsPane.getChildren().add(common.getAPane(str[0],str[1],str[2]));
                st=din.readUTF();
            }

            appointmentsPane.getChildren().forEach(
                    item -> {
                        AnchorPane pane = (AnchorPane) item;
                        if(((Label)(pane.getChildren().get(2))).getText().startsWith("Visited")){
                            pane.setDisable(true);
                        }
                        pane.setOnMouseEntered(
                                mouseEvent -> pane.setStyle("-fx-background-color: #a0a0a0;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        pane.setOnMouseExited(
                                mouseEvent -> pane.setStyle("-fx-background-color: #b3b3b3;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        pane.setOnMouseClicked(
                                mouseEvent -> {
                                    try {
                                        patientID = ((Label) (pane.getChildren().get(1))).getText();
                                        callSearch();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
            );

        }else{
            din.readUTF();
            Label label = new Label("No Appointments For Today Yet.");
            label.wrapTextProperty().setValue(true);
            appointmentsPane.getChildren().add(label);
        }

    }

    String patientID="";
    static boolean visited=false;

    public void callSearch() throws IOException {
        DataOutputStream dout = new DataOutputStream(common.getSocket().getOutputStream())/*common.getDout()*/;
        DataInputStream din = new DataInputStream(common.getSocket().getInputStream())/*common.getDin()*/;

        dout.writeUTF("DSP");
        dout.writeUTF(patientID+"./"+user.getDesignation());

        if(din.readBoolean()){
            dashboardController.patientData = din.readUTF();
            patientController.patID = patientID;
            Stage stage = (Stage) mainpane.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient.fxml")));
            Scene sc = stage.getScene();
            Scene scene  = new Scene(root,sc.getWidth(),sc.getHeight());
            stage.setScene(scene);
        }else{
            JFXSnackbar snackbar = new JFXSnackbar((Pane) mainpane);
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("No Appointment For "+patientID), Duration.millis(1500)));
        }
    }

    
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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patientsAndScans.test")));
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

}
