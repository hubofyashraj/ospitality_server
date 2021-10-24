package com.ospitality.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        common.primaryStage = primaryStage;

        primaryStage.getIcons().add(
                new Image(
                        Objects.requireNonNull(
                                this.getClass().getResourceAsStream(
                                        "/assets/osp.png"
                                )
                        )
                )
        );

        primaryStage.setTitle(
                "OSPITALITY SERVER"
        );

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getResource(
                                "login.fxml"
                        )
                )
        );

        primaryStage.setTitle(
                "Ospitality Server"
        );

        primaryStage.setScene(
                new Scene(
                        root, 600, 400
                )
        );

        primaryStage.initStyle(
                StageStyle.UNDECORATED
        );

        WindowStyle.allowDrag(
                root,primaryStage
        );

        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {

        directoryInitialize();
        launch(args);
    }

    public static void directoryInitialize() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String workingDirectory=os.contains("windows")?"C:\\ospitality\\":"ospitality/";
        String profilePicsDirectory = os.contains("windows")?"C:\\ospitality\\profile_pics\\":"ospitality/profile_pics/";
        Files.createDirectories(Paths.get(workingDirectory));
        Files.createDirectories(Paths.get(profilePicsDirectory));
        common.setWorkingDirectory(workingDirectory);
        common.setProfilePicsDirectory(profilePicsDirectory);

    }
}
