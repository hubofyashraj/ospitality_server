package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class updateDetailsAdmHandler extends Thread{
    Socket socket;
    DataOutputStream dout;
    DataInputStream din;
    public updateDetailsAdmHandler(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        dout = new DataOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run(){
        try {
            String test = din.readUTF();
            String[] arr = test.split("\\./");

            String Query = String.format("UPDATE HMS SET USERNAME='%s',PASSWORD='%s',ROLE='%s',ADDRESS='%s'," +
                            "GENDER='%s',MOBILENUMBER=%s,WORKEMAIL='%s',DESIGNATION='%s' WHERE USERID='%s'",
                    arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8]);

            try (Statement st = common.getCon().createStatement()) {
                st.executeUpdate(Query);
                dout.writeBoolean(true);
            }catch (SQLException e){
                dout.writeBoolean(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
        
    }
}
