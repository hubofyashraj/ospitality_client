package com.ospitality.client.labtechnician;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class dashboardController {

    public StackPane mainpane;
    public ImageView userImg;
    public Label userNameTxt;
    public Label roleTxt;
    public Label designationTxt;
    public Label emailTxt;
    public Label phnoTxt;
    public Label joiningDateTxt;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button logOutBTN;
    @FXML
    private Button settingsBTN;




    @FXML
    public TilePane tilePane,tilePane1;
    @FXML
    private TabPane tabPane;

    public static String data="";





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
        emailTxt.setText(user.getWorkEmail());
        phnoTxt.setText(user.getPhNo());
        designationTxt.setText(user.getDesignation());
        joiningDateTxt.setText(user.getJoiningDate());

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


        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
            tabPane.setTabMaxWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
        });

        DataOutputStream dout = common.getDout();
        DataInputStream din = common.getDin();

        ArrayList<AnchorPane> list = new ArrayList<>();
        ArrayList<AnchorPane> list1 = new ArrayList<>();

        try{
            dout.writeUTF("LLI");
            if(din.readBoolean()){
                String s = din.readUTF();
                while(!s.equals("S")){
                    String[] arr=s.split("\\./");
                    if(arr[4].equals("null")){
                        AnchorPane pane = common.getLTPane(arr[0],arr[1],arr[2],arr[3]);
                        list.add(pane);
                    }else{
                        AnchorPane pane = common.getLTPane(arr[0],arr[1],arr[2],arr[4]);
                        list1.add(pane);
                    }
                    s=din.readUTF();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(list.size()>0){
            list.forEach(
                    anchorPane -> {

                        tilePane.getChildren().add(0, anchorPane);
                        anchorPane.setOnMouseEntered(
                                mouseEvent -> anchorPane.setStyle("-fx-background-color: #403434;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        anchorPane.setOnMouseExited(
                                mouseEvent -> anchorPane.setStyle("-fx-background-color: #3d3d3d;-fx-background-radius: 10px;-fx-border-radius: 10px")
                        );

                        anchorPane.setOnMouseClicked(
                                mouseEvent -> {
                                    Label assignmentDate = (Label) anchorPane.getChildren().get(3);
                                    Label name = (Label) anchorPane.getChildren().get(0);
                                    Label id = (Label) anchorPane.getChildren().get(1);
                                    Label testName = (Label) anchorPane.getChildren().get(2);
                                    data = id.getText()+"./"+testName.getText()+"./"+assignmentDate.getText()+"./"+name.getText();

                                    try {
                                        callSearch();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
            );

        }else{
            tilePane.getChildren().add(0,new Label("No Tests Pending"));
        }

        if(list1.size()>0){
            list1.forEach(
                    anchorPane -> tilePane1.getChildren().add(anchorPane)
            );
        }else{
            tilePane1.getChildren().add(0,new Label("No Tests Done today"));
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
    void callLogOut() {
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
    void callSearch() throws IOException {
        Stage stage = (Stage) mainpane.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("testReport.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }
}
