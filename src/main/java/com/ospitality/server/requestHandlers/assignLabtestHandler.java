package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;

public class assignLabtestHandler extends Thread{
    Socket socket;

    public assignLabtestHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String data = din.readUTF();
            System.out.println(data);
            System.out.println(111);

            common.getCon().createStatement().executeUpdate(String.format(
                    "INSERT INTO labtests (assignmentDate,patient,testName) VALUES ('%s','%s','%s')",
                    LocalDate.now(), data.split("\\./")[0], data.split("\\./")[1])
            );
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        new HandleRequestThread(socket).start();
    }
}
