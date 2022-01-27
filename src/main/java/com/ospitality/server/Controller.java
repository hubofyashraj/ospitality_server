package com.ospitality.server;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class Controller {
    @FXML
    public StackPane mainPane;
    @FXML
    public Button stopServerBtn;
    @FXML
    public Button startServerBtn;
    @FXML
    public TextArea logsArea;
    public ImageView closeBtn;
    public ImageView minimizeBtn;


    @FXML
    public Label serverStatus;

    Thread accept=null;



    @FXML
    public void initialize(){

        DB oDB = new DB();
        try {
            common.setCon(oDB.getConnect());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        serverStatus.setText("SERVER STOPPED");
        serverStatus.setStyle("-fx-text-fill: RED");
        stopServerBtn.setDisable(true);
        common.setC(this);
    }

    @FXML
    public void callCloseServer() {
        System.exit(0);

    }

    public void callStartServer() {
        if(common.getSs()==null || common.getSs().isClosed()){
            if(startServer()){
                serverStatus.setText("SERVER STARTED");
                serverStatus.setStyle("-fx-text-fill: GREEN");
                stopServerBtn.setDisable(false);
                startServerBtn.setDisable(true);
                logsArea.appendText(String.format("\n%s\n######## SERVER STARTED #########\n", LocalDateTime.now()));
                new Logger(String.format("\n%s\n######## SERVER STARTED #########\n", LocalDateTime.now())).start();
            }

            acceptRequestsThread.flag=true;
            accept = new acceptRequestsThread();

            accept.start();
        }

    }

    public void callStopServer() {
        try {

            Socket socket = new Socket("localhost",5678);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF("SERVER");
            dout.writeUTF("KSN");

        } catch (IOException e) {
            e.printStackTrace();
            logsArea.appendText("\n"+LocalDateTime.now()+"\nPORT : 5678\t"+e.getLocalizedMessage()+"\n");
            new Logger("\n"+LocalDateTime.now()+"\nPORT : 5678\t"+e.getLocalizedMessage()+"\n").start();
        }
        serverStatus.setText("SERVER STOPPED");
        serverStatus.setStyle("-fx-text-fill: RED");
        stopServerBtn.setDisable(true);
        startServerBtn.setDisable(false);
        logsArea.appendText(String.format("\n%s\n######## SERVER STOPPED #########\n", LocalDateTime.now()));
        new Logger(String.format("\n%s\n######## SERVER STOPPED #########\n", LocalDateTime.now())).start();
    }

    public boolean startServer(){
        try {
            common.setSs(new ServerSocket(5678));
        } catch (IOException e) {
            e.printStackTrace();
            logsArea.appendText("\n"+LocalDateTime.now()+"\nPORT : 5678\t"+e.getLocalizedMessage()+"\n");
            new Logger("\n"+LocalDateTime.now()+"\nPORT : 5678\t"+e.getLocalizedMessage()+"\n").start();
        }

        return common.getSs()!=null;
    }

    public void mouseEnteredClose() throws URISyntaxException {
        closeBtn.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/cancelRed.png")).toURI().toString()));
    }
    public void mouseExitedClose() throws URISyntaxException {
        closeBtn.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/cancelDark.png")).toURI().toString()));
    }

    public void callMinimize() {
        ((Stage)(minimizeBtn.getScene().getWindow())).setIconified(true);

    }

    public void mouseEnteredMinimize() throws URISyntaxException {
        minimizeBtn.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/minimizeCyan.png")).toURI().toString()));
    }

    public void mouseExitedMinimize() throws URISyntaxException {
        minimizeBtn.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/minimizeDark.png")).toURI().toString()));
    }

}
