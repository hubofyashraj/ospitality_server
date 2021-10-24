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

public class searchPatientHandler extends Thread{
    Socket socket;

    public searchPatientHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT patients.* FROM patients " +
                    "INNER JOIN appointments ON patients.patient_id = appointments.patient " +
                    "WHERE appointments.patient LIKE '%s' AND appointments.department LIKE '%s'",
                    str.split("\\./")[0], str.split("\\./")[1]));


            if(rs.next()){
                String remarks = rs.getString("remarks");
                dout.writeBoolean(true);
                dout.writeUTF(rs.getString("name")+"./"+rs.getInt("age")+"./"+
                        rs.getString("dob")+"./"+rs.getString("gender")+"./"+
                        rs.getString("last_diagnosed")+"./"+
                        rs.getString("patient_id")+"./"+"Remarks : "+remarks);
            }else{
                dout.writeBoolean(false);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
