package com.ospitality.server.requestHandlers;

import com.ospitality.server.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class authHandler extends Thread{
    Socket socket;

    Controller c = common.getC();

    public authHandler(Socket authSocket){
        this.socket = authSocket;
    }

    @Override
    public void run() {
        super.run();

        String log;

        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String text = dis.readUTF();
            System.out.println(text);
            String[] arr = text.split("\\./");
            String id = arr[0];
            String pass = arr[1];

            boolean access = false;
            String details = "./";

            try {
                Statement st = common.getCon().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM HMS WHERE UserID LIKE '"+id+"'");

                if(rs.next()){
                    if(rs.getString("PassWord").equals(pass)){
                        access = true;

                        details = rs.getString("userName")+"./"+rs.getString("UserID")+"./"+
                                rs.getInt("NumericID")+"./"+rs.getBoolean("profileComplete")+"./"+
                                rs.getString("gender")+"./"+rs.getString("Role")+"./"+
                                rs.getString("Designation")+"./"+rs.getLong("mobileNumber")+"./"+
                                rs.getString("workEmail")+"./"+rs.getString("Address")+"./"+
                                rs.getString("Joining")+"./"+rs.getString("personalEmail");

                        log="\n\nClient Address : "+socket.getRemoteSocketAddress()+"\tUser : "+ common.checkHash(socket.getRemoteSocketAddress())+"\tLogin Request Accepted ";
                    }else{
                        log="\n\nClient Address : "+socket.getRemoteSocketAddress()+"\tUser : "+ common.checkHash(socket.getRemoteSocketAddress())+"\tLogin Request Declined \n\tReason : Wrong Credentials";
                    }
                }else{
                    log="\n\nClient Address : "+socket.getRemoteSocketAddress()+"\tUser : "+ common.checkHash(socket.getRemoteSocketAddress())+"\tLogin Request Declined \n\tReason : Wrong Credentials";
                }

                if(access){
                    rs = st.executeQuery("SELECT * FROM suspended_staff WHERE ID LIKE '"+id+"'");
                    if(rs.next()){
                        access=false;
                        log="\n\nClient Address : "+socket.getRemoteSocketAddress()+"\tUser : "+common.checkHash(socket.getRemoteSocketAddress())+"\tLogin Request Declined\n\tReason : Account Suspended \t DUE TO : "+rs.getString(2);
                    }
                }
                c.logsArea.appendText(log);
                new Logger(log).start();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());



            dos.writeBoolean(access);

            if(access) dos.writeUTF(details);


        } catch (IOException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
