package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class rcpDashbrdInitHandler extends Thread{
    Socket socket;

    public rcpDashbrdInitHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());


            Connection con = common.getCon();

            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(String.format("select * from everydayDetails where date='%s'",LocalDate.now()));

                boolean flag = rs.next();
                dout.writeBoolean(flag);

                if(flag){
                    do {
                        String str = rs.getInt(2)+"./"+rs.getInt(3);
                        dout.writeUTF(str);
                    }while(rs.next());
                    dout.writeUTF("!!");
                }


                rs = st.executeQuery("SELECT * FROM everydayDetails");
                flag=rs.next();
                dout.writeBoolean(flag);
                LocalDate lastDay = LocalDate.now();
                int i=0;
                if(flag){
                    do{
                        String str = rs.getDate(1)+"./"+rs.getInt(2)+"./"+rs.getInt(3)+"./"+
                                rs.getInt(4);
                        dout.writeUTF(str);
                        if(!rs.getDate(1).toLocalDate().equals(lastDay)) i++;
                    }while(rs.next() && i<7);
                    dout.writeUTF("!!");
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();

    }
}
