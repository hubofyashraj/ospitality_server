package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class addUserInitializationHandler extends Thread{

    Socket socket;

    public addUserInitializationHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        super.run();


        try {
            Statement stmt = common.getCon().createStatement();
            Statement st = common.getCon().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT userName,UserID,Role FROM HMS WHERE UserID NOT IN (SELECT iD FROM suspended_staff)");

            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            boolean flag = rs.next();
            dout.writeBoolean(flag);


            if(flag){
                String uid="";
                do {
                    uid=rs.getString(2);
                    dout.writeUTF(rs.getString(1) + "./" + uid + "./" + rs.getString(3)+"./U");
                    ResultSet rs1 = st.executeQuery("SELECT * FROM profile_pics WHERE user_id LIKE '"+uid+"'");
                    if(rs1.next()){
                        if(rs1.getBoolean(2)){
                            dout.writeBoolean(true);

                            File profilePic = new File(common.getProfilePicsDirectory() + uid + ".png");
                            boolean picExists = profilePic.exists();
                            dout.writeBoolean(picExists);
                            if(picExists){
                                dout.flush();
                                BufferedImage image = ImageIO.read(profilePic);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(image,"png",byteArrayOutputStream);

                                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(size);
                                outputStream.write(byteArrayOutputStream.toByteArray());
                                outputStream.flush();

                            }

                        }else{
                            dout.writeBoolean(false);
                        }
                    }
                } while (rs.next());
            }

            rs = stmt.executeQuery("SELECT userName,UserID,Role FROM hms WHERE UserID IN (SELECT iD FROM suspended_staff)");
            if(rs.next()){
                String uid="";
                do{
                    uid = rs.getString(2);
                    dout.writeUTF(rs.getString(1) + "./" + uid + "./" + rs.getString(3)+"./B");
                    ResultSet rs1 = st.executeQuery("SELECT * FROM profile_pics WHERE user_id LIKE '"+uid+"'");
                    if(rs1.next()){
                        if(rs1.getBoolean(2)){
                            dout.writeBoolean(true);

                            File profilePic = new File(common.getProfilePicsDirectory() + uid + ".png");
                            boolean picExists = profilePic.exists();
                            dout.writeBoolean(picExists);
                            if(picExists){
                                dout.flush();
                                BufferedImage image = ImageIO.read(profilePic);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(image,"png",byteArrayOutputStream);

                                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(size);
                                outputStream.write(byteArrayOutputStream.toByteArray());
                                outputStream.flush();

                            }

                        }else{
                            dout.writeBoolean(false);
                        }
                    }
                }while(rs.next());
            }

            dout.writeUTF("!!");

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        new HandleRequestThread(socket).start();

    }
}