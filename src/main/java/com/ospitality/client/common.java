package com.ospitality.client;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Objects;

import java.awt.image.BufferedImage;

import static javafx.geometry.Insets.EMPTY;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class common {

    private static String path;

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        common.path = path;
    }

    public static Socket socket;

    public static DataOutputStream dout;

    public static DataInputStream din;

    public static void setDin(DataInputStream din){
        common.din = din;
    }

    public static DataInputStream getDin(){
        return din;
    }

    public static void setDout(DataOutputStream dout){
        common.dout = dout;
    }

    public static DataOutputStream getDout(){
        return dout;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) throws IOException {
        common.socket = socket;
        common.setDin(new DataInputStream(socket.getInputStream()));
        common.setDout(new DataOutputStream(socket.getOutputStream()));
    }



    public static Stage mainStage;

    public static String workingDirectory="";

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    public static void setWorkingDirectory(String workingDirectory) {
        common.workingDirectory = workingDirectory;
    }

    private static String IP = "";

    public static void setIP(String IP){
        common.IP =IP;
    }

    public static String getIP() {
        return IP;
    }


    public static LocalDate getToday(){
        return LocalDate.now();
    }


    public static int getArraySum(int[] arr, int size) {
        int sum = 0;
        for(int i=0;i<size;i++) sum += arr[i];
        return sum;
    }

    public static AnchorPane getAnchorPane() throws URISyntaxException {

        TextField administration = new TextField();
        administration.setPromptText("Administration (t/days)");
        administration.setFocusTraversable(false);
        administration.setLayoutX(14);
        administration.setLayoutY(38);



        TextField time = new TextField();
        time.setPromptText("Days");
        time.setFocusTraversable(false);
        time.setLayoutX(206);
        time.setLayoutY(38);

        Button button = new Button("CLICK");
        button.setLayoutX(389);
        button.setLayoutY(38);

        AnchorPane pane=new AnchorPane(administration,time,button);
        pane.setPrefHeight(100);
        pane.setPrefWidth(450);
        pane.getStylesheets().add(Objects.requireNonNull(common.class.getResource("/stylesheet/style7.css")).toURI().toString());
        seTeffect(pane);

        return pane;
    }

    public static AnchorPane getAnchorPane1() throws URISyntaxException {

        TextField nos = new TextField();
        nos.setFocusTraversable(false);
        nos.setPromptText("Count");
        nos.setLayoutX(14);
        nos.setLayoutY(38);


        Button button = new Button("CLICK");
        button.setLayoutX(207);
        button.setLayoutY(38);

        AnchorPane pane=new AnchorPane(nos,button);
        pane.setPrefHeight(100);
        pane.setPrefWidth(275);
        pane.getStylesheets().add(Objects.requireNonNull(common.class.getResource("/stylesheet/style7.css")).toURI().toString());
        seTeffect(pane);

        return pane;
    }

    public static AnchorPane getAPane(String name, String id){
        Label name1 = new Label(name);
        Label id1 = new Label(id);
        AnchorPane pane = new AnchorPane(name1,id1);
        m(pane);
        return pane;
    }

    public static AnchorPane getUserPane(boolean accountStatus, String name, String id, String role, Image dp) throws URISyntaxException {
        Label name1 = new Label(name);
        name1.prefWidth(160);
        name1.setMinWidth(160);
        name1.setAlignment(Pos.CENTER);
        Label id1 = new Label(id);
        id1.prefWidth(160);
        id1.setMinWidth(160);
        id1.setAlignment(Pos.CENTER);
        Label role1 = new Label(role);
        role1.prefWidth(160);
        role1.setMinWidth(160);
        role1.setAlignment(Pos.CENTER);
        ImageView blocked = new ImageView();

        ImageView view = new ImageView(dp);
        AnchorPane pane = new AnchorPane(view,name1,id1,role1);
        if(accountStatus){
            blocked.setImage(new Image(Objects.requireNonNull(common.class.getResource("/icons/blockedDark.png")).toURI().toString()));
            pane.getChildren().add(blocked);
            AnchorPane.setRightAnchor(blocked,0.0);
            blocked.setLayoutY(0);
            blocked.setLayoutX(130);
            blocked.setOpacity(0.7);
            blocked.setFitWidth(30);
            blocked.setFitHeight(30);
        }
        view.setFitHeight(100);
        view.setFitWidth(100);
        view.setLayoutX(30);
        view.setLayoutY(15);
        name1.setLayoutY(120);
        id1.setLayoutY(135);
        role1.setLayoutY(150);

        AnchorPane.setLeftAnchor(name1,0.0);
        AnchorPane.setLeftAnchor(role1,0.0);
        AnchorPane.setLeftAnchor(id1,0.0);
        AnchorPane.setRightAnchor(id1,0.0);
        AnchorPane.setRightAnchor(name1,0.0);
        AnchorPane.setRightAnchor(role1,0.0);

        pane.setBackground(new Background(new BackgroundFill(new Color(0.24,0.24,0.24,1), new CornerRadii(10), EMPTY)));

        seTeffect(pane);

        return pane;
    }

    public static AnchorPane getLTPane(String name, String id, String testName, String assignedOn){
        Label name1 = new Label(name);
        Label id1 = new Label(id);
        Label testName1 = new Label(testName);
        testName1.wrapTextProperty().setValue(true);
        Label assignedOn1 = new Label(assignedOn);
        AnchorPane pane = new AnchorPane(name1,id1,testName1,assignedOn1);

        pane.setMinHeight(110);
        pane.setPrefHeight(120);
        pane.setMaxHeight(120);
        pane.setMinWidth(160);
        pane.setPrefWidth(160);

        pane.setStyle("-fx-background-color: #323232; -fx-border-radius: 10px; -fx-background-radius: 10px");
        seTeffect(pane);
        pane.getChildren().get(0).setLayoutY(75);
        pane.getChildren().get(1).setLayoutY(90);
        pane.getChildren().get(2).setLayoutY(35);
        pane.getChildren().get(3).setLayoutY(10);
        pane.getChildren().forEach(
                node -> {
                    node.maxWidth(160);
                    ((Label)node).setAlignment(Pos.CENTER);
                    ((Label)node).setPadding(new Insets(0,10,0,10));
                    AnchorPane.setRightAnchor(node,0.0);
                    AnchorPane.setLeftAnchor(node,0.0);
                }
        );
        return pane;
    }

    private static void m(AnchorPane pane) {
        pane.setBackground(new Background(new BackgroundFill(new Color(0.24,0.24,0.24,1), new CornerRadii(10), EMPTY)));
        pane.setStyle("-fx-background-color: #3d3d3d");
        seTeffect(pane);
        pane.setStyle("-fx-border-radius: 10px");
        pane.setMinWidth(100);
        pane.setMinHeight(100);
        pane.setPrefWidth(160);
        pane.setPrefHeight(160);
        pane.getChildren().get(0).setLayoutX(55);
        pane.getChildren().get(0).setLayoutY(120);
        pane.getChildren().get(1).setLayoutX(55);
        pane.getChildren().get(1).setLayoutY(140);
    }

    public static void seTeffect(AnchorPane pane) {
        DropShadow effect = new DropShadow();
        effect.setBlurType(BlurType.THREE_PASS_BOX);
        effect.setWidth(5);
        effect.setHeight(5);
        effect.setRadius(2);
        effect.setOffsetX(0);
        effect.setOffsetY(0);
        effect.setSpread(0);
        pane.setEffect(effect);
    }

    public static ImageView dp;
    public static File inputFile;

    public static void updateDP(File inputFile, ImageView userImg) throws IOException {
        dp = userImg;
        common.inputFile=inputFile;
        File pp = new File(getWorkingDirectory()+"profilePic.png");
        if(pp.exists()) pp.delete();
        Parent root1 = FXMLLoader.load(Objects.requireNonNull(common.class.getResource("selectImage.fxml")));
        Scene scene = new Scene(root1);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        WindowStyle.allowDrag(root1,stage);
        stage.show();
    }

    public static void uploadUserDP() throws IOException {
        dout.writeUTF("UPP");
        dout.writeUTF(user.getUserID());
        dout.flush();
        
        BufferedImage image = ImageIO.read(new File(getWorkingDirectory()+"profilePic.png"));
        BufferedImage bufferedImage = resizeImage(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

        
        OutputStream out = socket.getOutputStream();
        out.write(size);
        out.write(byteArrayOutputStream.toByteArray());
        out.flush();
    }

    public static void removeUserDp() throws IOException {
        File pp = new File(getWorkingDirectory()+"profilePic.png");
        if(pp.exists()) pp.delete();
        dout.writeUTF("RPP");
        dout.writeUTF(user.getUserID());
        dout.flush();
    }

    public static BufferedImage resizeImage(BufferedImage originalImage){
        BufferedImage resizedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, 100, 100, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static void loadDP(ImageView userImg) throws IOException, URISyntaxException {
        ///////////////-------LOADING PROFILE PIC-----///////////////

        dp=userImg;

        File pp = new File(getWorkingDirectory()+"profilePic.png");
        if(pp.exists()){
            Image profilePic = new Image(pp.toURI().toString());
            dp.setImage(profilePic);
        }else{
            dp.setImage(new Image(Objects.requireNonNull(common.class.getResource("/icons/user.png")).toURI().toString()));
        }
    }

    public static void logout(StackPane mainpane) {
        JFXDialogLayout content = new JFXDialogLayout();
        Text text = new Text("click LOGOUT if sure or click anywhere on screen ");
        text.setFill(Color.WHITESMOKE);
        content.setHeading(text);

        Button logout = new Button("LOGOUT");
        content.setActions(logout);
        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.BOTTOM);
        logout.setOnAction(
                actionEvent -> {
                    try{
                        DataOutputStream dout = common.getDout();

                        dout.writeUTF("LOGOUT");

                        common.getSocket().close();

                        Stage stage = common.mainStage;
                        Parent root = FXMLLoader.load(Objects.requireNonNull(common.class.getResource("login.fxml")));
                        Scene sc = stage.getScene();
                        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
                        stage.setScene(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.close();
                }
        );
        dialog.show();

    }
}
