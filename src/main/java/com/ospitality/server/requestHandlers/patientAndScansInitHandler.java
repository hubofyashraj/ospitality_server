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

public class patientAndScansInitHandler extends Thread{
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream din;
    public patientAndScansInitHandler(Socket clientSocket) throws IOException {
        this.socket=clientSocket;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run(){
        try {
            String expertise = din.readUTF();
            System.out.println(expertise);
            
            Statement st = common.getCon().createStatement();
            Statement st1 = common.getCon().createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT PATIENTS.NAME, PATIENTS.PATIENT_ID FROM PATIENTS INNER JOIN APPOINTMENTS ON APPOINTMENTS.PATIENT=PATIENTS.PATIENT_ID WHERE APPOINTMENTS.DATE LIKE (SELECT CURDATE()) AND APPOINTMENTS.DEPARTMENT LIKE '"+expertise+"' AND APPOINTMENTS.VISITED=0");

            boolean t1=rs1.next();
            ResultSet rs = st.executeQuery("SELECT DISTINCT PATIENTS.NAME, PATIENTS.PATIENT_ID FROM PATIENTS INNER JOIN VISITED ON VISITED.PATIENT=PATIENTS.PATIENT_ID WHERE VISITED.DATE LIKE (SELECT CURDATE()) AND VISITED.DEPARTMENT LIKE '" + expertise + "'");
            boolean t = rs.next();
            dataOutputStream.writeBoolean(t||t1);
            dataOutputStream.flush();
            System.out.println(t1+" "+t);
            if(t1){
                do{
                    dataOutputStream.writeUTF(rs1.getString(1)+"./"+rs1.getString(2)+"./UV");
                }while (rs1.next());
            }

            if(t){
                do{
                    dataOutputStream.writeUTF(rs.getString(1)+"./"+rs.getString(2)+"./V");
                }while(rs.next());
            }
            dataOutputStream.writeUTF("S");


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
