package com.ospitality.client.receptionist;

import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class dashboardController {

    public Button appointmentBTN;
    public ImageView userImg;
    public Label userNameTxt;
    public Label roleTxt;
    public StackPane navbarStackPane;
    public Label designationTxt;
    public Label emailTxt;
    public Label phnoTxt;
    public Label joiningDateTxt;
    public LineChart<String,Number> weaklyLineChart;
    public Label newPatientsTxt;
    public Label appointmentsTodayTxt;
    public StackPane mainpane;
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
    public void initialize() throws IOException, URISyntaxException {
        userNameTxt.setText(user.getUserName());
        roleTxt.setText(user.getRole());
        designationTxt.setText(user.getDesignation());
        phnoTxt.setText(user.getPhNo());
        emailTxt.setText(user.getWorkEmail());
        joiningDateTxt.setText(user.getJoiningDate());

        DataOutputStream dout = common.getDout();
        DataInputStream din = common.getDin();

        dout.writeUTF("RDI");
        if(din.readBoolean()){
                String read;
                while(!(read=din.readUTF()).equals("!!")){
                    appointmentsTodayTxt.setText(read.split("\\./")[0]);
                    newPatientsTxt.setText(read.split("\\./")[1]);
                }
        }

        XYChart.Series<String,Number> seriesAppointment = new XYChart.Series<>();
        seriesAppointment.setName("Appointments");

        XYChart.Series<String,Number> seriesNewPatients = new XYChart.Series<>();
        seriesNewPatients.setName("New Patients");

        XYChart.Series<String,Number> seriesVisits = new XYChart.Series<>();
        seriesVisits.setName("Visits");
        
        if(din.readBoolean()){
                String data;
                while(!(data=din.readUTF()).equals("!!")){
                    String[] arr = data.split("\\./");

                    String day = LocalDate.parse(arr[0]).getDayOfWeek().name();

                    seriesAppointment.getData().add(new Data<>(day, Integer.parseInt(arr[1])));
                    seriesNewPatients.getData().add(new Data<>(day, parseInt(arr[2])));
                    seriesVisits.getData().add(new Data<>(day, parseInt(arr[3])));
                }

                weaklyLineChart.getData().add(seriesAppointment);
                weaklyLineChart.getData().add(seriesNewPatients);
                weaklyLineChart.getData().add(seriesVisits);

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
    void callSettings() throws IOException {
        Stage stage = (Stage) settingsBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/ospitality/client/settings.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    public void callAppointment() throws IOException{
        Stage stage = (Stage) appointmentBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("appointment.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

}
