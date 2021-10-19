package com.ospitality.client;

import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class completeProfileController {
    public Label errorphno;
    public static String IDLoggedIn;
    public static String nameLoggedIn;

    @FXML
    private ComboBox<String> gendr;

    @FXML
    private Label NameUser,UID;

    @FXML
    private TextField phNo, Designation,secEmail,Address;

    @FXML
    private Button submitBTN;

    Pattern pattern = Pattern.compile("[a-zA-Z].*@[a-zA-Z]+\\.com");

    @FXML
    public void initialize(){
        submitBTN.setDisable(true);
        IDLoggedIn=user.getUserID();
        nameLoggedIn=user.getUserName();
        NameUser.setText(nameLoggedIn);
        UID.setText(IDLoggedIn);

        phNo.textProperty().addListener(
                (observableValue, s, t1) -> {
                    if (!t1.matches("\\d*")){
                        phNo.setText(t1.replaceAll("[^\\d]",""));
                    }
                    submitBTN.setDisable(
                            (Designation.getText().isEmpty() && Designation.getPromptText().isEmpty())
                                    || Address.getText().isEmpty()
                                    || phNo.getText().length()!=10
                                    || secEmail.getText().isEmpty()
                                    || gendr.getValue().isEmpty()
                    );
                }
        );

        if(IDLoggedIn.startsWith("DOC")){
            Designation.setPromptText("Already Given!");
            Designation.setDisable(true);
        }

    }




    public void CompleteSubmit() {

        Matcher matcher = pattern.matcher(secEmail.getText());
        if(matcher.matches()){
            String Gender = gendr.getValue();
            String Desig = Designation.getText();
            String PHNo = phNo.getText();
            String secondaryEmail = secEmail.getText();
            String Addr = Address.getText();

            try {
                errorphno.setText("");
                try{
                    String str = Gender+"./"+Desig+"./"+PHNo+"./"+secondaryEmail+"./"+Addr+"./"+IDLoggedIn;
                    DataOutputStream dout = common.getDout();
                    DataInputStream din = common.getDin();

                    dout.writeUTF("COMPPROF");
                    dout.writeUTF(str);

                    if(din.readBoolean()){
                        user.profileCompleted=true;
                        user.gender=Gender;
                        user.designation=Desig;
                        user.phNo =PHNo;
                        user.workEmail=secondaryEmail;
                        user.address=Addr;

                    }

                    Stage stage = (Stage) submitBTN.getScene().getWindow();
                    loginController.Dashboard(stage);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch(NumberFormatException e){
                errorphno.setText("Enter Correct Number");
            }

        }else{
            JFXSnackbar jfxSnackbar = new JFXSnackbar((Pane) secEmail.getParent());
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Enter Correct Email"),Duration.millis(500)));
        }
    }

    public void emailKeyPressed(KeyEvent keyEvent) {
        String email = secEmail.getText();
        Matcher matcher = pattern.matcher(email);

        submitBTN.setDisable(!matcher.matches());

        submitBTN.setDisable(
                (Designation.getText().isEmpty() && Designation.getPromptText().isEmpty())
                        || Address.getText().isEmpty()
                        || phNo.getText().length()!=10
                        || secEmail.getText().isEmpty()
                        || gendr.getValue().isEmpty()
        );

    }

    public void designationKeyPressed(KeyEvent keyEvent) {
        submitBTN.setDisable(
                (Designation.getText().isEmpty() && Designation.getPromptText().isEmpty())
                || Address.getText().isEmpty()
                || phNo.getText().length()!=10
                || secEmail.getText().isEmpty()
                || gendr.getValue().isEmpty()
        );
    }

    public void addressKeyPressed(KeyEvent keyEvent) {
        submitBTN.setDisable(
                (Designation.getText().isEmpty() && Designation.getPromptText().isEmpty())
                        || Address.getText().isEmpty()
                        || phNo.getText().length()!=10
                        || secEmail.getText().isEmpty()
                        || gendr.getValue().isEmpty()
        );
    }
}
