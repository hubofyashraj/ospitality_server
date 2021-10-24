package com.ospitality.server;

import java.io.*;

public class Logger extends Thread {
    String log;
    String fileName=null;

    public Logger(String log) {
        this.log = log;
    }

    public Logger(String log,String fileName){
        this.log=log;
        this.fileName=fileName;
    }

    public void run() {
        System.out.println(log);
        File file = new File(common.workingDirectory + (fileName==null?"ospitality_server.log":fileName));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(log);
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
