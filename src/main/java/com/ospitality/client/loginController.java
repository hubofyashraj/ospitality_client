package com.ospitality.client;

import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class loginController {

    public Button quitBTN;
    static String uid="";
    public AnchorPane troublePane;
    public StackPane mainPane;
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
                String ipAddr=ip.getText()==null?"localhost":ip.getText();
                if(ip.getText()!=null){
                    try(FileOutputStream fos = new FileOutputStream(common.getWorkingDirectory()+"ip")){
                        fos.write(ip.getText().getBytes(StandardCharsets.UTF_8));
                    }
                }common.setSocket(new Socket(ipAddr,5678));

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
            case "Doctor" -> "doctor";
            case "Admin" -> "admin";
            case "Receptionist" -> "receptionist";
            case "Lab Technician" -> "labtechnician";
            case "Medical Storekeeper" -> "medicalstorekeeper";
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
}
