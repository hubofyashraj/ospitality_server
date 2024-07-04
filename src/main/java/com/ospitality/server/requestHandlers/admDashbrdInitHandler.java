package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class admDashbrdInitHandler extends Thread{
    Socket socket;

    public admDashbrdInitHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM VISITS");

            boolean flag = rs.next();
            dout.writeBoolean(flag);
            if(flag){
                do{
                    String utf = rs.getDate(1)+"./"+rs.getInt(2)+"./"+
                            rs.getInt(3)+"./"+rs.getInt(4)+"./"+
                            rs.getInt(5)+"./"+rs.getInt(6)+"./"+
                            rs.getInt(7)+"./"+rs.getInt(8)+"./"+
                            rs.getInt(9)+"./"+rs.getInt(10);
                    dout.writeUTF(utf);
                }while(rs.next());
                dout.writeUTF("!!");
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        new HandleRequestThread(socket).start();
        
    }
}
