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
            ResultSet rs = st.executeQuery(String.format("SELECT PATIENTS.* FROM PATIENTS " +
                    "INNER JOIN APPOINTMENTS ON PATIENTS.PATIENT_ID = APPOINTMENTS.PATIENT " +
                    "WHERE APPOINTMENTS.PATIENT LIKE '%S' AND APPOINTMENTS.DEPARTMENT LIKE '%s'",
                    str.split("\\./")[0], str.split("\\./")[1]));


            if(rs.next()){
                String remarks = rs.getString("REMARKS");
                dout.writeBoolean(true);
                dout.writeUTF(rs.getString("NAME")+"./"+rs.getInt("AGE")+"./"+
                        rs.getString("DOB")+"./"+rs.getString("GENDER")+"./"+
                        rs.getString("LAST_DIAGNOSED")+"./"+
                        rs.getString("PATIENT_ID")+"./"+"remarks : "+remarks);
            }else{
                dout.writeBoolean(false);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
