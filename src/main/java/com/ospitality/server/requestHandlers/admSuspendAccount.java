package com.ospitality.server.requestHandlers;

import com.ospitality.server.HandleRequestThread;
import com.ospitality.server.Logger;
import com.ospitality.server.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class admSuspendAccount extends Thread{
    Socket socket;

    public admSuspendAccount(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run() {
        super.run();
        DataOutputStream out = null;
        DataInputStream din;
        String log="";
        try {
            din = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String[] data = din.readUTF().split("\\./");
            System.out.println(Arrays.toString(data));
            Statement st = common.getCon().createStatement();

            if(data[0].equals("B")){
                st.executeUpdate("INSERT INTO SUSPENDED_STAFF VALUES ('"+data[1]+"','"+data[2]+"',CURDATE())");
                log="\n\nUser : "+data[1]+"\tAccount Blocked ";
            }else if(data[0].equals("UB")){
                st.executeUpdate("DELETE FROM SUSPENDED_STAFF WHERE ID LIKE '"+data[1]+"'");
                log="\n\nUser : "+data[1]+"\tAccount Unblocked ";
            }

            out.writeBoolean(true);
            common.getC().logsArea.appendText(log);
            new Logger(log).start();


        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                assert out != null;
                out.writeBoolean(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        new HandleRequestThread(socket).start();
    }
}
