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

public class mdcBillInitHandler extends Thread{
    Socket socket;
    public mdcBillInitHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din  =new DataInputStream(socket.getInputStream());

            String id = din.readUTF();

            Statement st = common.getCon().createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM PATIENTS WHERE PATIENT_ID LIKE '"+id+"%' OR NAME LIKE '"+id+"%'");
            boolean t = rs.next();
            dout.writeBoolean(t);

            if(t){
                String str = "";
                do{
                    str+=rs.getString("NAME")+"__"+rs.getInt("AGE")+"./";
                }while(rs.next());
                str=str.substring(0,str.length()-2);
                dout.writeUTF(str);
            }


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
