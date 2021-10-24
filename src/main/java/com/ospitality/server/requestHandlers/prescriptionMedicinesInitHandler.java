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

public class prescriptionMedicinesInitHandler extends Thread{
    Socket socket;

    public prescriptionMedicinesInitHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String s = din.readUTF();

            Statement st = common.getCon().createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM medicines WHERE `Medicine Name` LIKE '"+s+"%'");
            if(rs.next()){
                dout.writeBoolean(true);
                String str = rs.getString("Medicine Name");
                while(rs.next()){
                    str=str+"./"+rs.getString("Medicine Name");
                }
                dout.writeUTF(str);
            }else{
                dout.writeBoolean(false);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
