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

public class docDashbrdInitHandler extends Thread{
    Socket socket;

    public docDashbrdInitHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){

        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();


            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery(String.format(
                    "SELECT * FROM APPOINTMENTS WHERE DATE LIKE '%s' AND " +
                            "DEPARTMENT LIKE '%s'", LocalDate.now(),str));
            int totalAppointment=0;
            int totalRemaining=0;
            boolean t = rs.next();
            dout.writeBoolean(t);
            if(t){
                totalAppointment++;
                if(!rs.getBoolean(5)){
                    totalRemaining++;
                }
                while (rs.next()){
                    totalAppointment++;
                    if(!rs.getBoolean(5)){
                        totalRemaining++;
                    }
                }
                dout.writeUTF(totalAppointment+"./"+totalRemaining);
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
