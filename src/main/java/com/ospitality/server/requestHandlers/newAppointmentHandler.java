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

public class newAppointmentHandler extends Thread{
    Socket socket;

    public newAppointmentHandler(Socket clientSokcet){
        this.socket=clientSokcet;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");
            Statement st = common.getCon().createStatement();
            boolean patientExist = st.executeQuery("SELECT NAME FROM PATIENTS WHERE PATIENT_ID LIKE '"+arr[1]+"'").next();
            dout.writeBoolean(patientExist);
            if(patientExist){
                st.executeUpdate(String.format("INSERT INTO APPOINTMENTS (DATE,PATIENT,DEPARTMENT) VALUES('%s','%s','%s')"
                        , arr[0], arr[1], arr[2]));
                dout.writeBoolean(true);

                ResultSet rs;
                rs = st.executeQuery(String.format("SELECT * FROM EVERYDAYDETAILS WHERE DATE LIKE '%s'", arr[0]));
                int appCount=0;
                if(rs.next()){
                    appCount = rs.getInt(2);
                    appCount++;
                }
                rs = st.executeQuery(String.format("SELECT * FROM EVERYDAYDETAILS WHERE DATE LIKE '%s'", arr[0]));
                if(rs.next()){
                    st.executeUpdate(String.format("UPDATE EVERYDAYDETAILS SET TOTALAPPOINTMENTS='%d' WHERE DATE LIKE '%s'"
                            , appCount,arr[0]));
                }else{
                    st.executeUpdate(String.format("INSERT INTO EVERYDAYDETAILS VALUES ('%s',1,0,0)", arr[0]));
                }
                dout.writeBoolean(true);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();

        }

        new HandleRequestThread(socket).start();
    }
}
