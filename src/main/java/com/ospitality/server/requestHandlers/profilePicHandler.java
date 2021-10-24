package com.ospitality.server.requestHandlers;

import com.ospitality.server.HandleRequestThread;
import com.ospitality.server.common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class profilePicHandler extends Thread{
    String requestType;
    Socket socket;
    public profilePicHandler(String requestType, Socket clientSocket){
        this.requestType=requestType;
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        DataInputStream din;
        DataOutputStream dOut = null;
        String id = null;
        Statement st = null;
        try {
            dOut = new DataOutputStream(socket.getOutputStream());
            din = new DataInputStream(socket.getInputStream());
            st = common.getCon().createStatement();
            id = din.readUTF();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        switch (requestType) {
            case "GPP"://GET PROFILE PIC
                try {
                    ResultSet rs = Objects.requireNonNull(st).executeQuery("SELECT * FROM profile_pics WHERE user_id LIKE '" + id + "'");
                    if (rs.next()) {
                        boolean exist =rs.getBoolean(2);
                        dOut.writeBoolean(exist);
                        if (exist) {
                            File profilePic = new File(common.getProfilePicsDirectory() + id + ".png");
                            boolean picExists = profilePic.exists();
                            dOut.writeBoolean(picExists);
                            if(picExists){
                                dOut.flush();
                                BufferedImage image = ImageIO.read(profilePic);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(image,"png",byteArrayOutputStream);

                                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(size);
                                outputStream.write(byteArrayOutputStream.toByteArray());
                                outputStream.flush();

                            }
                        }
                    }

                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "UPP": //UPDATE PROFILE PIC
                InputStream inputStream;
                byte[] sizeAr = new byte[4];
                BufferedImage image;
                try {
                    inputStream = socket.getInputStream();
                    int s = inputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                    byte[] imageAr = new byte[size];

                    int i = inputStream.read(imageAr);
                    image = ImageIO.read(new ByteArrayInputStream(imageAr));
                    File oldProfilePic = new File(common.getProfilePicsDirectory() + id + ".png");
                    if(oldProfilePic.exists()) oldProfilePic.delete();
                    File newProfilePic = new File(common.getProfilePicsDirectory()+id+".png");
                    ImageIO.write(image, "png", newProfilePic);
                    Objects.requireNonNull(st).executeUpdate("UPDATE profile_pics SET is_uploaded=1 WHERE user_id LIKE '"+id+"'");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "RPP": //REMOVE PROFILE PIC
                try {
                    File pf = new File(common.getProfilePicsDirectory() + id + ".png");
                    if(pf.exists()) pf.delete();
                    Objects.requireNonNull(st).executeUpdate("UPDATE profile_pics SET is_uploaded=0 WHERE user_id LIKE '" + id + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

        new HandleRequestThread(socket).start();
    }
}
