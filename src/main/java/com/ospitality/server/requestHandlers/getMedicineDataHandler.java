package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class getMedicineDataHandler extends Thread{
    Socket socket;

    public  getMedicineDataHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String med = din.readUTF();

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM MEDICINES WHERE `MEDICINE NAME` LIKE '%s'".formatted(med));
            if(rs.next()){
                String str = rs.getString(3)+"./"+rs.getString(6);
                dout.writeUTF(str);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
