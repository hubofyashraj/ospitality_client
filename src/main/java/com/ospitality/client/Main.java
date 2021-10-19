package com.ospitality.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.util.Objects.requireNonNull;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.getIcons().add(
                new Image(String.valueOf(getClass().getResource("/icons/osp.png")))
        );

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        common.mainStage = primaryStage;

        try  {
            File ipFile = new File(common.getWorkingDirectory()+"ip");
            if(ipFile.exists()){
                Scanner reader = new Scanner(ipFile);
                while (reader.hasNextLine()){
                    common.setIP(reader.nextLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = FXMLLoader.load(requireNonNull(loginController.class.getResource("login.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSPITALITY");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        directoryInitialize();
        launch(args);
    }

    public static void directoryInitialize() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String workingDirectory=os.contains("windows")?"C:\\ospitality_client\\":"ospitality_client/";
        Files.createDirectories(Paths.get(workingDirectory));
        common.setWorkingDirectory(workingDirectory);
    }


}
