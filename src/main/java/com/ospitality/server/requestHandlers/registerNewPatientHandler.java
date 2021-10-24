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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class registerNewPatientHandler extends Thread{
    Socket socket;

    public registerNewPatientHandler(Socket clientSocket) throws IOException {
        this.socket = clientSocket;

    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");

            int PatientNumeric_id = newPatientID();
            String patient_id = "Pat"+PatientNumeric_id;

            String Query = String.format("INSERT INTO patients (name,age,dob,gender,numeric_id,last_diagnosed,patient_id)" +
                            " VALUES ('%s','%d','%s','%s','%d','%s %s','%s')", arr[0], Integer.parseInt(arr[1]), arr[2], arr[3], PatientNumeric_id,
                    arr[4], DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()), patient_id);
            try{
                Statement st = common.getCon().createStatement();
                st.executeUpdate(Query);

                System.out.println(PatientNumeric_id);

                ResultSet rs = st.executeQuery(String.format("SELECT * FROM everydayDetails WHERE date like '%s'", LocalDate.now()));


                int patCount;
                if(rs.next()){
                    patCount = rs.getInt(3);
                    patCount++;
                    st.executeUpdate(String.format("UPDATE everydayDetails SET newPatients='%d' WHERE date like '%s'", patCount, LocalDate.now()));
                }else{
                    st.executeUpdate(String.format("INSERT INTO everydayDetails VALUES ('%s',0,1,0)", LocalDate.now()));
                }
                dout.writeBoolean(true);
                dout.writeUTF(patient_id);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e+"\n\t\tfalse");
                dout.writeBoolean(false);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }

    int newPatientID(){
        int ID=1000;
        try{
            Statement stmt = common.getCon().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT numeric_id FROM patients");
            while(rs.next()){
                ID=Math.max(rs.getInt("numeric_id"), ID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ID++;
        return ID;
    }


}
