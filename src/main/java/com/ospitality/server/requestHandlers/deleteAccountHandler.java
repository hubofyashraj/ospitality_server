package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class deleteAccountHandler extends Thread{
    Socket socket;

    public deleteAccountHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    @Override
    public void run(){

        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String[] data = din.readUTF().split("\\./");

            try (Statement st = common.getCon().createStatement()) {
                st.executeUpdate(String.format("UPDATE PROFILE_PICS SET IS_UPLOADED=0 WHERE USER_ID LIKE '%s'", data[0]));
                st.executeUpdate("INSERT INTO SUSPENDED_STAFF VALUES ('" + data[0] + "','"+data[1]+"',CURDATE())");
                dout.writeBoolean(true);
            } catch (SQLException e) {
                dout.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new HandleRequestThread(socket).start();
    }
}
