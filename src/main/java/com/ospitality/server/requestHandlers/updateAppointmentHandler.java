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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class updateAppointmentHandler extends Thread{
    Socket socket;

    public updateAppointmentHandler(Socket clientSocket){
        this.socket= clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");
            String oldDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(arr[3]));
            String newDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(arr[1]));
            Statement st = common.getCon().createStatement();
            st.executeUpdate(
                    String.format("UPDATE appointments SET department='%s',date='%s' WHERE patient like '%s' " +
                                    "AND date like '%s' AND department like '%s'",
                            arr[0], newDate, arr[2], oldDate, arr[4]));

//            LocalDate newDate = LocalDate.parse(arr[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            ResultSet rs = st.executeQuery(String.format("SELECT * FROM everydayDetails WHERE date like '%s'",oldDate));
            if (rs.next()){
                int appointmentsCount = rs.getInt(2);
                appointmentsCount--;
                st.executeUpdate(String.format("UPDATE everydayDetails SET totalAppointments='%d' WHERE date like '%s'",appointmentsCount,oldDate));

                ResultSet rs1=st.executeQuery(String.format("SELECT * FROM everydayDetails WHERE date like '%s'",newDate));
                if(rs1.next()){
                    appointmentsCount=rs1.getInt(2);
                    appointmentsCount++;
                    st.executeUpdate(String.format("UPDATE everydayDetails SET totalAppointments='%d' WHERE date like '%s'",appointmentsCount,newDate));
                }else{
                    st.executeUpdate(String.format("INSERT INTO everydayDetails VALUES ('%s',1,0,0)", newDate));
                }
            }
            dout.writeBoolean(true);

        } catch (IOException | SQLException | ParseException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
