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
import java.util.Arrays;

public class updatePatientRemarksHandler extends Thread{
    Socket socket;
    public updatePatientRemarksHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){


        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din  =new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");
            System.out.println(str);
            System.out.println(Arrays.toString(arr));

            Statement st = common.getCon().createStatement();
            st.executeUpdate(String.format("UPDATE appointments SET visited = 1 WHERE " +
                    "date LIKE '%s' AND patient LIKE '%s' AND department LIKE '%s'",
                    LocalDate.now(), arr[0], arr[1]));

            st.executeUpdate(String.format("UPDATE patients SET remarks='%s',last_diagnosed='%s %s' WHERE patient_id LIKE '%s'", arr[2], LocalDate.now(),
                    DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()), arr[0]));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();

    }
}
