package com.ospitality.client;

import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class settingsController {
    @FXML
    public StackPane mainpane;
    @FXML
    public TabPane tabPane;
    @FXML
    public Label name;
    @FXML
    public Label userId;
    @FXML
    public Label gender;
    @FXML
    public Label role;
    @FXML
    public Label designation;
    @FXML
    public Label phno;
    @FXML
    public Label emailWork;
    @FXML
    public Label emailPers;
    @FXML
    public Label joiningDate;
    @FXML
    public Label addr;
    @FXML
    public Button resetBtn;
    @FXML
    public PasswordField oldPassword;
    @FXML
    public PasswordField newPassword;
    @FXML
    public PasswordField confNewPass;
    public Label wrongOldPass;
    public Label newPassErr;
    public StackPane navbarStackPane;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button logOutBTN;
    @FXML
    private Button settingsBTN;
    @FXML
    private Button notificationBTN;

    JFXSnackbar jfxSnackbar;
    Label result = new Label();
    @FXML
    public void initialize(){
        jfxSnackbar = new JFXSnackbar((Pane) resetBtn.getParent());
        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
            tabPane.setTabMaxWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
        });

        name.setText(name.getText()+user.getUserName());
        userId.setText(userId.getText()+user.getUserID());
        gender.setText(gender.getText()+user.getGender());
        role.setText(role.getText()+user.getRole());
        phno.setText(phno.getText()+user.getPhNo());
        emailWork.setText(emailWork.getText()+user.getWorkEmail());
        emailPers.setText(emailPers.getText()+user.getPersonalEmail());
        addr.setText(addr.getText()+user.getAddress());
        joiningDate.setText(joiningDate.getText()+user.getJoiningDate());
        designation.setText(designation.getText()+user.getDesignation());

    }


    
    @FXML
    void callDashboard() throws IOException {
        Stage stage = (Stage) dashboardBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(common.getPath() + "/dashboard.fxml")));

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

    @FXML
    public void callTryReset() {
        wrongOldPass.setText("");
        newPassErr.setText("");

        String userid = user.getUserID();

        try {
            DataOutputStream dout = common.getDout();
            DataInputStream din = common.getDin();

            if(newPassword.getText().equals(confNewPass.getText()) && newPassword.getText().length()>0){
                if(oldPassword.getText().length()==0){
                    result.setText("Enter Old Password!!!");
                }else{
                    dout.writeUTF("PASSWD");
                    dout.writeUTF(userid+"./"+oldPassword.getText()+"./"+newPassword.getText());

                    if(din.readBoolean()){
                        result.setText("Password changed successfully");
                        result.setStyle("-fx-text-fill: green");
                    }else{
                        result.setText("Wrong password entered!");
                        result.setStyle("-fx-text-fill: red");
                    }
                }
            }else{
                if(!newPassword.getText().equals(confNewPass.getText())){
                    result.setText("Password Do Not Match!!");
                    result.setStyle("-fx-text-fill: red");
                }else{
                    result.setText("Some Of The Fields Seems Empty!!");
                    result.setStyle("-fx-text-fill: red");
                }
            }
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        oldPassword.setText("");
        newPassword.setText("");
        confNewPass.setText("");
    }
}
