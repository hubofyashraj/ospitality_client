package com.ospitality.client.admin;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

public class accountsManagementController {

     /////////////////////////////////////////////////////////////////
    ///////////////////////////// ADD USER TAB //////////////////////
   /////////////////////////////////////////////////////////////////

    @FXML
    public StackPane mainpane;
    @FXML
    public TabPane tabPane;
    @FXML
    public StackPane navbarStackPane;
    @FXML
    public Label errorSavingDetailsTxt;
    @FXML
    public Button submitBtn;
    @FXML
    public Button searchBtn;
    public Button blockButton;
    public Button deleteButton;
    public ImageView photo;

    @FXML
    private Button dashboardBTN;

    @FXML
    private Button logOutBTN;

    @FXML
    private Button settingsBTN;

    @FXML
    private Button notificationBTN;

    final DataOutputStream dout = common.getDout();
    final DataInputStream din = common.getDin();


    JFXSnackbar jfxSnackbar;
    Label result = new Label();
    @FXML
    public void initialize() throws IOException, URISyntaxException {
        jfxSnackbar = new JFXSnackbar((Pane) submitBtn.getParent());
        jfxSnackbar2 = new JFXSnackbar(mainPane);

        tilePane.getChildren().clear();

        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
            tabPane.setTabMaxWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
        });

        class users{
            String userName;
            String userID;
            String Role;
            boolean accountStatus;

            public boolean isAccountStatus() {
                return accountStatus;
            }

            public void setAccountStatus(boolean accountStatus) {
                this.accountStatus = accountStatus;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getUserID() {
                return userID;
            }

            public void setUserID(String userID) {
                this.userID = userID;
            }

            public String getRole() {
                return Role;
            }

            public void setRole(String role) {
                Role = role;
            }
        }

        ArrayList<users> list = new ArrayList<>();
        ArrayList<Image> dpList = new ArrayList<>();
        dout.writeUTF("AAUI");

        if(din.readBoolean()){
            String test;
            while(!(test = din.readUTF()).equals("!!")){
                String[] arr = test.split("\\./");

                users ob = new users();
                ob.setUserName(arr[0]);
                ob.setUserID(arr[1]);
                ob.setRole(arr[2]);
                ob.setAccountStatus(arr[3].equals("B"));
                list.add(ob);

                if(din.readBoolean()){
                    if(din.readBoolean()){

                        File dp = new File(common.getWorkingDirectory()+"userDp.png");
                        InputStream in = common.getSocket().getInputStream();
                        byte[] sizeAr = new byte[4];
                        int s = in.read(sizeAr);
                        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                        byte[] imageAr = new byte[size];
                        int i = in.read(imageAr);
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));



                        ImageIO.write(image,"png",dp);

                        dpList.add(new Image(dp.toURI().toString()));

                    }else{
                        Image image = new Image(getClass().getResource("/icons/user.png").toURI().toString());
                        dpList.add(image);
                    }
                }else{
                    Image image = new Image(getClass().getResource("/icons/user.png").toURI().toString());
                    dpList.add(image);
                }
            }
        }

        if(list.size()>0){
            int[] n =new int[1];
            list.forEach(users -> {
                try {
                    AnchorPane pane = common.getUserPane(users.accountStatus,users.getUserName(), users.getUserID(), users.getRole(), dpList.get(n[0]++));
                    pane.setOnMouseClicked(
                            mouseEvent -> {
                                ImageView dp = (ImageView) pane.getChildren().get(0);
                                photo.setImage(dp.getImage());
                                callSearchUser(((Label) pane.getChildren().get(2)).getText());
                                if(userIDTxt.getText().equals(user.getUserID())){
                                    deleteButton.setDisable(true);
                                    blockButton.setDisable(true);
                                }else{
                                    deleteButton.setDisable(false);
                                    blockButton.setDisable(false);
                                }
                            }
                    );
                    pane.setOnMouseEntered(mouseEvent -> pane.setStyle("-fx-background-color: #909090;-fx-background-radius: 10px;-fx-border-radius: 10px"));
                    pane.setOnMouseExited(mouseEvent -> pane.setStyle("-fx-background-color: #b4b4b4;-fx-background-radius: 10px;-fx-border-radius: 10px"));
                    tilePane.getChildren().add(pane);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
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
    void callSettings() throws IOException {
        Stage stage = (Stage) settingsBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/ospitality/client/settings.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }


    public Label userAddedTxt;
    public ComboBox<?> departmentCB;

    @FXML
    private ComboBox<?> jobChoice;

    @FXML
    private Label passnotmatch;

    @FXML
    private TextField Name,email,two_pass,two_passconf;


    @FXML
    public void submitSignUp() {
        String userName = Name.getText();
        String job = (String) jobChoice.getValue();
        String emailAddress = email.getText();
        String pass = two_pass.getText();
        String confPass = two_passconf.getText();

        if (userName.length()>0 && job.length()>0 && emailAddress.length()>0 && pass.length()>0 && confPass.length()>0){
            if(pass.equals(confPass)){
                passnotmatch.setText("");
                try {

                    String test = String.format("%s./%s./%s./%s./%s",
                            userName, job, emailAddress, pass, departmentCB.getValue());

                    dout.writeUTF("AAU");
                    dout.writeUTF(test);

                    boolean status = din.readBoolean();

                    String userAddedGreet;
                    if(status){
                        userAddedGreet = String.format("User Account for %s has been created.\nUSER ID : %s\nPASSWORD : %s", userName, din.readUTF(), confPass);
                        result.setText(userAddedGreet);
                        result.setStyle("-fx-text-fill: green;");
                        jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(10000)));
                        initialize();
                    }else{
                        userAddedGreet = "Can't Create User Account !!!";
                        result.setText(userAddedGreet);
                        result.setStyle("-fx-text-fill: red;");
                        jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));
                    }
                    Name.setText("");
                    jobChoice.setValue(null);
                    jobChoice.setPromptText("Select Role");
                    departmentCB.setValue(null);
                    departmentCB.setPromptText("Select Expertise");
                    email.setText("");
                    two_pass.setText("");
                    two_passconf.setText("");

                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }else{
                result.setText("passwords don't match!!!");
                result.setStyle("-fx-text-fill: red;");
                jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));
                passnotmatch.setText("");
            }
        }else{
            result.setText("Fill All Details!!");
            result.setStyle("-fx-text-fill: red;");
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));
        }

    }

    public void callCheckRole() {
        String job= (String) jobChoice.getValue();
        if(job==null){
            departmentCB.setVisible(false);
        }else{
            departmentCB.setVisible(job.equals("Doctor"));
        }
    }

      ////////////////////////////////////////////////////////////////////
     //////////////////////// MODIFY DETAILS TAB ////////////////////////
    ////////////////////////////////////////////////////////////////////

    public StackPane detailsPane;
    public TextField userNameTxt;
    public TextField passwordTxt;
    public TextField designationTxt;
    public TextField phnoTxt;
    public TextField workEmailTxt;
    public TextArea addressTxt;
    public ImageView lockImg;
    public Button editBtn;
    public Button saveBtn;
    public ComboBox<String> genderCB;
    public ComboBox<String> roleCB;
    public ScrollPane scrollPane;
    public TilePane tilePane;
    public StackPane mainPane;
    Image image;

    JFXSnackbar jfxSnackbar2;

    @FXML
    private TextField userIDTxt;




    @FXML
    void callDeleteUser() throws URISyntaxException {
        JFXDialogLayout content = new JFXDialogLayout();
        content.getStylesheets().add(mainpane.getStylesheets().get(0));
        Text text = new Text("Give a short reason");
        text.setFill(Color.valueOf("#282331"));
        content.setHeading(text);
        TextArea reason = new TextArea();
        content.setBody(reason);
        Button cancel = new Button("cancel");
        Button delete = new Button("delete");
        content.setActions(cancel,delete);
        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.BOTTOM);

        cancel.setOnAction(
                e -> dialog.close()

        );
        delete.setOnAction(
                actionEvent -> {
                    if(reason.getText().isEmpty()){
                        text.setFill(Color.RED);
                        return;
                    }
                    try {
                        dout.writeUTF("ADD");
                        dout.writeUTF(userIDTxt.getText()+"./"+reason.getText());

                        if(din.readBoolean()){
                            result.setText("Account Deleted Successfully");
                            result.setStyle("-fx-text-fill: green");
                        }else{
                            result.setText("Some error occurred while deleting data (server issue)");
                            result.setStyle("-fx-text-fill: red");
                        }
                        JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBtn.getParent());
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(3000)));
                        closeModify();
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                    scrollPane.setVisible(true);
                    detailsPane.setVisible(false);

                    try {
                        initialize();
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    dialog.close();
                }

        );
        dialog.show();
    }

    @FXML
    void callEdit() throws URISyntaxException {
        if(userNameTxt.isDisabled()){
            addressTxt.setDisable(false);
            designationTxt.setDisable(false);
            userNameTxt.setDisable(false);
            passwordTxt.setDisable(false);
            phnoTxt.setDisable(false);
            workEmailTxt.setDisable(false);
            genderCB.setDisable(false);
            roleCB.setDisable(false);

            image = new Image(Objects.requireNonNull(getClass().getResource("/icons/unlock.png")).toURI().toString());

        }else{
            addressTxt.setDisable(true);
            designationTxt.setDisable(true);
            userNameTxt.setDisable(true);
            passwordTxt.setDisable(true);
            phnoTxt.setDisable(true);
            workEmailTxt.setDisable(true);
            genderCB.setDisable(true);
            roleCB.setDisable(true);

            image = new Image(Objects.requireNonNull(getClass().getResource("/icons/lock.png")).toURI().toString());
        }
        lockImg.setImage(image);


    }

    @FXML
    void callSave( ) {
        JFXDialogLayout content = new JFXDialogLayout();
        Text text = new Text("Press Yes to save or No to cancel");
        text.setFill(Color.valueOf("#282331"));
        content.setHeading(text);
        Button yes = new Button("Yes");
        Button no = new Button("No");
        content.setActions(yes,no);
        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.BOTTOM);

        no.setOnAction(
                e -> dialog.close()
        );

        yes.setOnAction(
                e -> {
                    String test = userNameTxt.getText()+"./"+passwordTxt.getText()+"./"+roleCB.getValue()+"./"+addressTxt.getText()+"./"+
                            genderCB.getValue()+"./"+phnoTxt.getText()+"./"+workEmailTxt.getText()+"./"+designationTxt.getText()+"./"+
                            userIDTxt.getText();

                    try {
                        dout.writeUTF("AUD");
                        dout.writeUTF(test);

                        if(din.readBoolean()){
                            result.setText("Saved Successfully");
                            result.setStyle("-fx-text-fill: green");
                        }else{
                            result.setText("Some error occurred while saving data server issue");
                            result.setStyle("-fx-text-fill: red");
                        }
                        JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBtn.getParent());
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(3000)));

                        scrollPane.setVisible(true);
                        detailsPane.setVisible(false);
                        closeModify();
                        initialize();
                    } catch (URISyntaxException | IOException ex) {
                        ex.printStackTrace();
                    }
                    dialog.close();
                }
        );

        dialog.show();
    }

    @FXML
    void callSearchUser( ) {
        if(!userIDTxt.getText().isEmpty()){
            String user = userIDTxt.getText();
            request(user);
        }else{
            JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBtn.getParent());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("User id Can't Be Empty!!"), Duration.millis(1000)));
        }
    }

    void callSearchUser(String userId) {
        userIDTxt.setText(userId);
        request(userId);
    }

    private void request(String user) {
        try {
            DataOutputStream dout = common.getDout();
            DataInputStream din = common.getDin();

            dout.writeUTF("AGU");
            dout.writeUTF(user);

            if(din.readBoolean()){
                String test = din.readUTF();
                String[] arr = test.split("\\./");
                scrollPane.setVisible(false);
                detailsPane.setVisible(true);
                userNameTxt.setText(arr[0]);
                passwordTxt.setText(arr[1]);
                roleCB.getSelectionModel().select(arr[2]);
                roleCB.setStyle("-fx-backgrouond-color: transparent");
                designationTxt.setText(arr[3]);
                genderCB.getSelectionModel().select(arr[4]);
                workEmailTxt.setText(arr[5]);
                phnoTxt.setText(arr[6]);
                addressTxt.setText(arr[7]);
                if(arr[8].equals("B")){
                    blockButton.setText("Unblock Account");
                    reasonBlock=arr[9];
                    dateBlock=arr[10];
                }else{
                    blockButton.setText("Block Account");
                }
            }else{
                result.setText("User not found!");
                result.setStyle("-fx-text-fill: red");
                JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBtn.getParent());
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(3000)));
            }

        } catch (IOException throwable) {
            throwable.printStackTrace();
        }
    }

    String reasonBlock="";
    String dateBlock="";

    public void closeModify() throws URISyntaxException {

        addressTxt.setDisable(true);
        designationTxt.setDisable(true);
        userNameTxt.setDisable(true);
        passwordTxt.setDisable(true);
        phnoTxt.setDisable(true);
        workEmailTxt.setDisable(true);
        genderCB.setDisable(true);
        roleCB.setDisable(true);

        image = new Image(Objects.requireNonNull(getClass().getResource("/icons/lock.png")).toURI().toString());

        lockImg.setImage(image);

        detailsPane.setVisible(false);
        scrollPane.setVisible(true);
    }
    public void callBlockUser(){
        JFXDialogLayout content = new JFXDialogLayout();
        content.getStylesheets().add(mainpane.getStylesheets().get(0));
        Text text = new Text("Give a short reason");
        text.setFill(Color.valueOf("#282331"));
        content.setHeading(text);
        TextArea reason = new TextArea();
        content.setBody(reason);
        Button cancel = new Button("cancel");
        Button block = new Button("block");
        content.setActions(cancel,block);
        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.BOTTOM);
        if(blockButton.getText().startsWith("U")){
            text.setText("Press unblock to Unblock User");
            block.setText("unblock");
            reason.setDisable(true);
            reason.setText("User Blocked on: "+dateBlock+"\n\nReason: "+reasonBlock);
        }
        cancel.setOnAction(
                e -> dialog.close()

        );
        block.setOnAction(
                actionEvent -> {
                    if(reason.getText().isEmpty()){
                        text.setFill(Color.RED);
                        return;
                    }
                    try {
                        dout.writeUTF("ASA");
                        String t="";
                        if(blockButton.getText().startsWith("B")){
                            t="B";
                            dout.writeUTF(t+"./"+userIDTxt.getText()+"./"+reason.getText());
                        }else{
                            t="UB";
                            dout.writeUTF(t+"./"+userIDTxt.getText());
                        }

                        if(din.readBoolean()){
                            if(t.equals("B"))   result.setText("Account Blocked");
                            else result.setText("Account Unblocked");
                            result.setStyle("-fx-text-fill: green");
                        }else{
                            result.setText("Some error occurred (server issue)");
                            result.setStyle("-fx-text-fill: red");
                        }
                        JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBtn.getParent());
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(3000)));
                        closeModify();
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                    scrollPane.setVisible(true);
                    detailsPane.setVisible(false);

                    try {
                        initialize();
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    dialog.close();
                }

        );
        dialog.show();
    }

}
