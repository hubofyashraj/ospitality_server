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

public class passwdHandler extends Thread{
    Socket socket;

    public passwdHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String test = din.readUTF();
            String[] arr = test.split("\\./");

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT PASSWORD FROM HMS WHERE USERID LIKE '%s'", arr[0]));
            if(rs.next()){
                if(rs.getString(1).equals(arr[1])){
                    st.executeUpdate("UPDATE HMS SET PASSWORD='"+arr[2]+"' WHERE USERID LIKE '"+arr[0]+"'");
                    dout.writeBoolean(true);
                }else{
                    dout.writeBoolean(false);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
        
    }
}
