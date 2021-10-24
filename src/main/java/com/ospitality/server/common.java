package com.ospitality.server;

import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class common {

    private static ServerSocket ss = null;
    private static String profilePicsDirectory;

    public static ServerSocket getSs() {
        return ss;
    }

    public static void setSs(ServerSocket ss) {
        common.ss = ss;
    }

    static Controller c;

    public static Controller getC() {
        return c;
    }

    public static void setC(Controller c) {
        common.c = c;
    }

    public static Stage primaryStage;

    static String uName;
    static String uPass;

    public static String getUName() {
        return uName;
    }

    public static void setUName(String uName) {
        common.uName = uName;
    }

    public static String getUPass() {
        return uPass;
    }

    public static void setUPass(String uPass) {
        common.uPass = uPass;
    }

    static String workingDirectory;

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    public static void setWorkingDirectory(String workingDirectory) {
        common.workingDirectory = workingDirectory;
    }

    static Connection con;

    public static Connection getCon() {
        return con;
    }

    public static void setCon(Connection con) {
        common.con = con;
    }


    private static final HashMap<SocketAddress,String > map = new HashMap<>();

    public static void insertHash(SocketAddress address, String s){
        map.put(address,s);
    }

    public static String checkHash(SocketAddress address){
        String s = "!!";
        if(map.containsKey(address)){
            s=map.get(address);
        }
        return s;
    }

    private static final ArrayList<Socket> socketList = new ArrayList<>();

    public static ArrayList<Socket> getSocketList() {
        return socketList;
    }

    public static void setSocketToList(Socket socket) {
        common.socketList.add(socket);
    }

    public static void setProfilePicsDirectory(String profilePicsDirectory) {
        common.profilePicsDirectory = profilePicsDirectory;
    }
    public static String getProfilePicsDirectory(){
        return profilePicsDirectory;
    }
}


