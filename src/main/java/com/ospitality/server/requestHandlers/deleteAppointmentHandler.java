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

public class deleteAppointmentHandler extends Thread{
    Socket socket;

    public deleteAppointmentHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(arr[1]));

            try {
                Statement st = common.getCon().createStatement();
                st.executeUpdate(
                        String.format("DELETE FROM appointments  WHERE patient like '%s' AND date like '%s' AND " +
                                "department like '%s'", arr[0], date, arr[2])
                );
                dout.writeBoolean(true);


                ResultSet rs = st.executeQuery(
                        String.format("SELECT * FROM everydayDetails WHERE date like '%s'",date)
                );

                if (rs.next()){
                    int appointmentsCount=rs.getInt(2);
                    appointmentsCount--;
                    st.executeUpdate(
                            String.format("UPDATE everydayDetails SET totalAppointments='%d' WHERE date like '%s'",
                                    appointmentsCount,date)
                    );
                    dout.writeBoolean(true);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
