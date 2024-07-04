package com.ospitality.client;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;


public class loginController {

    public Button quitBTN;
    static String uid="";
    public AnchorPane troublePane;
    public StackPane mainPane;
    public TextField port;
    String pass="";

    @FXML
    private TextField loginIdField;
    @FXML
    private TextField ip;

    @FXML
    private PasswordField loginPassField;

    @FXML
    private Button loginbtn;
    @FXML
    private Button troublebtn;

    JFXSnackbar jfxSnackbar;
    Label result = new Label();
    public void initialize() {
        jfxSnackbar = new JFXSnackbar((Pane) loginbtn.getParent());
        if(!common.getIP().equals("")){
            ip.setText(common.getIP());
        }
    }

    @FXML
    void callLogin() {
        uid = loginIdField.getText();
        pass = loginPassField.getText();
        if(uid.length()!=0 && pass.length()!=0){
            try{
                String ipAddr= Objects.equals(ip.getText(), "") ?"localhost":ip.getText();
                if(ip.getText()!=null){
                    try(FileOutputStream fos = new FileOutputStream(common.getWorkingDirectory()+"ip")){
                        fos.write(ip.getText().getBytes(StandardCharsets.UTF_8));
                    }
                }
                String portAddr = Objects.equals(port.getText(), "") ?"5678":port.getText();

                try{
                    common.setSocket(new Socket(ipAddr,Integer.parseInt(portAddr)));

                }catch (ConnectException e){
                    JFXSnackbar snackbar = new JFXSnackbar(mainPane);
                    Label l = new Label("Server Not Reachable");
                    l.setStyle("-fx-text-fill: red");
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l,Duration.millis(1000)));
                    return;
                }

                Socket socket = common.getSocket();

                OutputStream out = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                DataOutputStream dout = new DataOutputStream(out);
                DataInputStream din = new DataInputStream(is);

                String test = uid+"./"+pass;
                dout.writeUTF(uid);
                dout.writeUTF("AUTH");
                dout.writeUTF(test);
                boolean access = din.readBoolean();
                if(access){
                    String input = din.readUTF();
                    String[] details = input.split("\\./");
                    System.out.println(Arrays.toString(details));
                    user.userName=details[0];
                    user.userID=details[1];
                    user.profileCompleted=details[3].equals("true");
                    user.gender=details[4];
                    user.role=details[5];
                    user.designation=details[6];
                    user.phNo =details[7];
                    user.workEmail=details[8];
                    user.address=details[9];
                    user.joiningDate=details[10];
                    user.personalEmail=details[11];
                    user.setPassword(pass);

                    File pp = new File(common.getWorkingDirectory()+"profilePic.png");
                    if(pp.exists()) pp.delete();

                    dout.writeUTF("GPP");
                    dout.writeUTF(user.getUserID());
                    if(din.readBoolean()){
                        // IF PIC IS IN DATABASE
                        if(din.readBoolean()){
                            // IF LOCAL FILE IS FOUND ON SERVER
                            File profilePic = new File(common.getWorkingDirectory()+"profilePic.png");
                            InputStream in = socket.getInputStream();
                            byte[] sizeAr = new byte[4];
                            int s = in.read(sizeAr);
                            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                            byte[] imageAr = new byte[size];
                            int i = in.read(imageAr);
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

                            ImageIO.write(image,"png",profilePic);

                        }
                    }else {
                        System.out.println("no pp");
                    }

                    Stage stage = (Stage)loginbtn.getScene().getWindow();
                    Dashboard(stage);
                }else{
                    result.setText("PLEASE CHECK CREDENTIALS!!!!");
                    result.setStyle("-fx-text-fill: red;");
                    jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));
                }

            }
            catch (Exception e){    //11
                e.printStackTrace();
            }
        }
        else{
            result.setText("ID OR PASSWORD CAN'T BE EMPTY!!!!");
            result.setStyle("-fx-text-fill: red;");
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(1000)));
        }
    }


    @FXML
    static void Dashboard(Stage stage) throws Exception{
        String dashboardPath = switch (user.getRole()) {
            case "DOCTOR" -> "doctor";
            case "ADMIN" -> "admin";
            case "RECEPTIONIST" -> "receptionist";
            case "LAB TECHNICIAN" -> "labtechnician";
            case "MEDICAL STOREKEEPER" -> "medicalstorekeeper";
            default -> throw new IllegalStateException("Unexpected value: " + user.getRole());
        };

        common.setPath(dashboardPath);
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(loginController.class.getResource("/com/ospitality/client/"+dashboardPath + "/dashboard.fxml")));

        if(!user.profileCompleted){
            root=FXMLLoader.load(Objects.requireNonNull(loginController.class.getResource("/com/ospitality/client/completeProfile.fxml")));
        }
        Scene sc = stage.getScene();
        assert root != null;
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void troubleLogin() {

        troublePane.setVisible(true);
    }


    public void callQuit() {
        System.exit(0);
    }


    public void callHide() {
        troublePane.setVisible(false);
        mainPane.setDisable(false);
    }

    public void callIPSetting() {
        JFXDialogLayout content = new JFXDialogLayout();
        Text text = new Text("Enter Server IP Address. Enter Port Number Also if Server is not local, i.e. Via Tunnel.\nLeave port empty if Server is on local network");
        text.setFill(Color.valueOf("#282331"));
        content.setHeading(text);
        TextField ipAddr = new TextField(ip.getText());
        Label ipL = new Label("IP");
        StackPane.setAlignment(ipL, Pos.CENTER_LEFT);
        StackPane.setMargin(ipL,new Insets(0,0,0,20));
        Label portL = new Label("PORT");
        StackPane.setAlignment(portL, Pos.CENTER_LEFT);
        StackPane.setMargin(portL,new Insets(0,0,0,250));
        ipAddr.setPromptText("default localhost");
        ipAddr.setPrefWidth(180);
        ipAddr.setMaxWidth(180);
        StackPane.setAlignment(ipAddr, Pos.CENTER_LEFT);
        StackPane.setMargin(ipAddr,new Insets(0,0,0,40));
        TextField portAddr = new TextField(port.getText());
        portAddr.setPrefWidth(160);
        portAddr.setMaxWidth(160);
        StackPane.setAlignment(portAddr, Pos.CENTER_LEFT);
        StackPane.setMargin(portAddr,new Insets(0,0,0,300));
        StackPane pane = new StackPane(ipL,ipAddr,portL,portAddr);
        content.setBody(pane);
        Button save = new Button("SAVE");
        Button reset = new Button("RESET");

        content.setActions(save,reset);

        JFXDialog dialog = new JFXDialog(mainPane,content, JFXDialog.DialogTransition.CENTER);

        save.setOnAction(
                actionEvent -> {
                    if(!Objects.equals(ipAddr.getText(), "")){
                        ip.setText(ipAddr.getText());
                        if(!Objects.equals(portAddr.getText(), "")){
                            port.setText(portAddr.getText());
                        }

                    }
                    dialog.close();

                    JFXSnackbar snackbar = new JFXSnackbar(mainPane);
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("IP Settings Saved Successfully")));
                }
        );

        reset.setOnAction(
                actionEvent -> {
                    ip.setText("");
                    ipAddr.setText("");
                    port.setText("");
                    portAddr.setText("");

                    dialog.close();
                    JFXSnackbar snackbar = new JFXSnackbar(mainPane);
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("IP Settings Reset Successful")));
                }
        );

        dialog.show();
    }
}
