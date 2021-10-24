package com.ospitality.server;

import java.net.Socket;

public class acceptRequestsThread extends Thread{

    static boolean flag=true;

    public void run() {
        while (true){
            Socket s;
            try {
                if(flag) {
                    s = common.getSs().accept();
                    common.setSocketToList(s);
                    new HandleRequestThread(s).start();
                }else break;
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}
