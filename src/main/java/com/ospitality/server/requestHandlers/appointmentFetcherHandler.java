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

public class appointmentFetcherHandler extends Thread{
    Socket socket;

    public appointmentFetcherHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String id = din.readUTF();
            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT NAME FROM PATIENTS WHERE PATIENT_ID LIKE '"+id+"'");
            boolean patientExist = rs.next();
            dout.writeBoolean(patientExist);
            if(patientExist){
                dout.writeUTF(rs.getString(1));
                rs.close();
                rs = st.executeQuery("SELECT * FROM APPOINTMENTS WHERE PATIENT='" + id + "'");
                boolean flag = rs.next();
                dout.writeBoolean(flag);
                if(flag){
                    do{
                        String app = rs.getDate(1) + "  "+rs.getString(3)+"./"+rs.getBoolean(5);
                        dout.writeUTF(app);
                    }while(rs.next());
                    dout.writeUTF("!!");
                }
            }


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
