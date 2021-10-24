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

public class addUserHandler extends Thread{
    Socket socket;

    public addUserHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run() {
        super.run();

        try {
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            String text = din.readUTF();
            String[] arr = text.split("\\./");

            String userName = arr[0];
            String job = arr[1];
            String emailAddress = arr[2];
            String pass = arr[3];
            String dept = arr[4];

            int uID = newUserId();

            String fullUID = "";

            switch (job) {
                case "Doctor":
                    fullUID = "DOC" + uID;
                    break;
                case "Admin":
                    fullUID = "ADM" + uID;
                    break;
                case "Receptionist":
                    fullUID = "RCP" + uID;
                    break;
                case "Lab Technician":
                    fullUID = "LBT" + uID;
                    break;
                case "Medical Storekeeper":
                    fullUID = "MDC" + uID;
                    break;
            }

            String Query = String.format("INSERT INTO HMS (userName,UserID,NumericID,Role,personalEmail,PassWord,Joining) " +
                    "VALUES ('%s','%s','%d','%s','%s','%s','%s')",
                    userName, fullUID, uID, job, emailAddress, pass, LocalDate.now());
            if(job.equals("Doctor")) Query = String.format("INSERT INTO HMS (userName,UserID,NumericID,Role,personalEmail," +
                    "PassWord,Joining,Designation) VALUES ('%s','%s','%d','%s','%s','%s','%s','%s')",
                    userName, fullUID, uID, job, emailAddress, pass, LocalDate.now(), dept);

            try (Statement st = common.getCon().createStatement()) {
                st.executeUpdate(Query);
                st.executeUpdate("INSERT INTO profile_pics VALUES('"+fullUID+"',0)");
                dOut.writeBoolean(true);
                dOut.writeUTF(fullUID);
            }catch (SQLException e){
                dOut.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new HandleRequestThread(socket).start();

        
    }

    private int newUserId() {
        int id=1000;

        try {
            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT NumericID FROM HMS ORDER BY NumericID; ");
            while(rs.next()){
                id = rs.getInt("NumericID");
            }
            id+=1;

            st.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }


        return id;
    }
}
