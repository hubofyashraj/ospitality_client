package com.ospitality.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.ospitality.client.common.*;

public class selectImageController {

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private ImageView imageView;
    Image pngDpFile;
    File file;
    @FXML
    public void initialize() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputFile);
        file = new File(common.getWorkingDirectory()+user.getUserID()+".png");
        ImageIO.write(bufferedImage, "png", file);
        pngDpFile = new Image(file.getAbsoluteFile().toURI().toString());
        imageView.setImage(pngDpFile);
        inputFile = file;
    }

    @FXML
    void callCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    void callSaveDP() throws IOException {
        file.renameTo(new File(getWorkingDirectory()+"profilePic.png"));
        dp.setImage(pngDpFile);
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
        common.uploadUserDP();
    }

}
