package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class lbtMarkTestDoneHandler extends Thread{
    Socket socket;
    public lbtMarkTestDoneHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String[] arr = din.readUTF().split("\\./");

            Statement st = common.getCon().createStatement();

            st.executeUpdate("UPDATE labtests SET testDoneOn=(SELECT CURDATE()) WHERE patient LIKE '"+arr[0]+"' AND testName LIKE '"+arr[1]+"' AND assignmentDate LIKE '"+arr[2]+"'");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        new HandleRequestThread(socket).start();

    }
}
