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

            st.executeUpdate("UPDATE LABTESTS SET TESTDONEON=(SELECT CURDATE()) WHERE PATIENT LIKE '"+arr[0]+"' AND TESTNAME LIKE '"+arr[1]+"' AND ASSIGNMENTDATE LIKE '"+arr[2]+"'");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        new HandleRequestThread(socket).start();

    }
}
