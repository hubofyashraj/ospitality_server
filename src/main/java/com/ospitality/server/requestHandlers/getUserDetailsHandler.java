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

public class getUserDetailsHandler extends Thread{
    Socket socket;

    public getUserDetailsHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String userId = din.readUTF();

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM HMS WHERE USERID LIKE '%s'".formatted(userId));

            boolean flag = rs.next();
            dout.writeBoolean(flag);

            if(flag){
                String test = rs.getString("userName")+"./"+rs.getString("PassWord")+"./"+
                        rs.getString("Role")+"./"+rs.getString("Designation")+"./"+
                        rs.getString("gender")+"./"+rs.getString("workEmail")+"./"+
                        rs.getString("mobileNumber")+"./"+rs.getString("Address");

                rs = st.executeQuery("SELECT SUSPENSION_REASON,SUSPENSION_DATE FROM SUSPENDED_STAFF WHERE ID LIKE '%s'".formatted(userId));
                if(rs.next()){
                    test = test+"./B./"+rs.getString(1)+"./"+rs.getString(2);
                }else{
                    test+="./U";
                }
                dout.writeUTF(test);
            }


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
        
    }
}
