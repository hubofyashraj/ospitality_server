package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class completeProfileHandler extends Thread{
    Socket socket;

    public completeProfileHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String str = din.readUTF();
            String[] arr = str.split("\\./");

            String query = String.format("UPDATE HMS SET PROFILECOMPLETE=1, GENDER='%s',DESIGNATION='%s'," +
                            "MOBILENUMBER=%s,WORKEMAIL='%s',ADDRESS='%s' WHERE USERID='%s'"
                    , arr[0], arr[1].toUpperCase(), arr[2], arr[3], arr[4], arr[5]);

            if(arr[5].startsWith("DOC")){
                query=String.format("UPDATE HMS SET PROFILECOMPLETE=1, GENDER='%s'," +
                                "MOBILENUMBER=%s,WORKEMAIL='%s',ADDRESS='%s' WHERE USERID='%s'"
                        , arr[0], arr[2], arr[3], arr[4], arr[5]);
            }

            Statement st = common.getCon().createStatement();
            st.executeUpdate(query);

            dout.writeBoolean(true);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();

    }
}
