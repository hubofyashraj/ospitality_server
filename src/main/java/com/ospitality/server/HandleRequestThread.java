package com.ospitality.server;

import com.ospitality.server.requestHandlers.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.time.LocalDateTime;



public class HandleRequestThread extends Thread {

    protected Socket socket;
    OutputStream out = null;
    InputStream in = null;

    public HandleRequestThread(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run() {
        super.run();

        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
            DataInputStream din = new DataInputStream(in);

            Controller c= common.getC();

            SocketAddress address = socket.getRemoteSocketAddress();


            String user = common.checkHash(address);
            if(user.equals("!!")){
                String client = din.readUTF();
                common.insertHash(address,client);
                c.logsArea.appendText("\n\nConnection Request From : "+client);
                user= common.checkHash(address);
            }
            String requestType = din.readUTF();

            c.logsArea.appendText(String.format("\n\n%s\nClient Address : %s\tUser : %s\tRequest : %s", LocalDateTime.now(), address, user, requestType));
            new Logger(String.format("\n\n%s\nClient Address : %s\tUser : %s\tRequest : %s", LocalDateTime.now(), address, user, requestType)).start();

            if(requestType.endsWith("PP")){
                Thread PP = new profilePicHandler(requestType,socket);
                PP.setName("PP");
                PP.start();
            }else if(requestType.startsWith("A")){
                switch (requestType) {
                    case "AUTH":
                        Thread AUTH = new authHandler(socket);
                        AUTH.setName("AUTH");
                        AUTH.start();
                        break;
                    case "ADI":
                        Thread ADI = new admDashbrdInitHandler(socket);
                        ADI.setName("ADMDASHBRDINIT");
                        ADI.start();
                        break;
                    case "AAU":
                        Thread ADDUSER = new addUserHandler(socket);
                        ADDUSER.setName("ADDUSER");
                        ADDUSER.start();
                        break;
                    case "AAUI":
                        Thread AAUI = new addUserInitializationHandler(socket);
                        AAUI.setName("ADDUSERINIT");
                        AAUI.start();
                        break;
                    case "AGU":
                        Thread AGU = new getUserDetailsHandler(socket);
                        AGU.setName("ADDUSERINIT");
                        AGU.start();
                        break;
                    case "AUD":
                        Thread AUD = new updateDetailsAdmHandler(socket);
                        AUD.setName("UPDATEDETAILSADM");
                        AUD.start();
                        break;
                    case "ADD":
                        Thread ADD = new deleteAccountHandler(socket);
                        ADD.setName("DELETEACCOUNT");
                        ADD.start();
                        break;
                    case "ASA":
                        Thread ASA = new admSuspendAccount(socket);
                        ASA.setName("SUSPENDACCOUNT");
                        ASA.start();
                        break;
                }
            }else if (requestType.startsWith("R")){
                if (requestType.endsWith("A")){
                    switch (requestType) {
                        case "RNA":

                            Thread RNA = new newAppointmentHandler(socket);
                            RNA.setName("NEWAPPOINTMENT");
                            RNA.start();
                            break;
                        case "RDA":
                            Thread RDA = new deleteAppointmentHandler(socket);
                            RDA.setName("DLAPNTMNT");
                            RDA.start();
                            break;
                        case "RUA":

                            Thread RUA = new updateAppointmentHandler(socket);
                            RUA.setName("UPDAPNTMNT");
                            RUA.start();
                            break;
                        case "RFA":

                            Thread RFA = new appointmentFetcherHandler(socket);
                            RFA.setName("FETCHAPNTMNT");
                            RFA.start();
                            break;
                    }
                } else if ("RNP".equals(requestType)) {

                    Thread RNP = new registerNewPatientHandler(socket);
                    RNP.setName("RGSTRPAT");
                    RNP.start();
                } else if ("RDI".equals(requestType)) {

                    Thread RDI = new rcpDashbrdInitHandler(socket);
                    RDI.setName("RCPDASHBRDINIT");
                    RDI.start();
                }
            }else if (requestType.startsWith("D")){
                switch (requestType) {
                    case "DDI":

                        Thread DDI = new docDashbrdInitHandler(socket);
                        DDI.setName("DDI");
                        DDI.start();
                        break;
                    case "DPSI":

                        Thread DPSI = new patientAndScansInitHandler(socket);
                        DPSI.setName("DPSI");
                        DPSI.start();
                        break;
                    case "DSP":

                        Thread DSP = new searchPatientHandler(socket);
                        DSP.setName("DSP");
                        DSP.start();
                        break;
                    case "DER":

                        Thread DER = new updatePatientRemarksHandler(socket);
                        DER.setName("DER");
                        DER.start();
                        break;
                    case "DMV":

                        Thread DMV = new markVisitHandler(socket);
                        DMV.setName("DMV");
                        DMV.start();
                        break;
                    case "DAL":

                        Thread DAL = new assignLabtestHandler(socket);
                        DAL.setName("DAL");
                        DAL.start();
                        break;
                    case "DPMI":

                        Thread DPMI = new prescriptionMedicinesInitHandler(socket);
                        DPMI.setName("DPMI");
                        DPMI.start();
                        break;
                }
            }else if (requestType.startsWith("L")){
                switch (requestType) {
                    case "LLI":

                        Thread LLI = new lbtGetTestsHandler(socket);
                        LLI.setName("LLI");
                        LLI.start();
                        break;
                    case "LTD":

                        Thread LTD = new lbtMarkTestDoneHandler(socket);
                        LTD.setName("LTD");
                        LTD.start();
                        break;
                    case "LOGOUT":
                        socket.close();
                        break;
                }
            }else if (requestType.startsWith("M")){
                switch (requestType) {
                    case "MGMD":

                        Thread MGMD = new getMedicineDataHandler(socket);
                        MGMD.setName("MGMD");
                        MGMD.start();
                        break;
                    case "MSP":

                        Thread MSP = new mdcBillInitHandler(socket);
                        MSP.setName("MSP");
                        MSP.start();
                        break;
                    case "MMI":

                        Thread MMI = new mdcMedicineListInitHandler(socket);
                        MMI.setName("MMI");
                        MMI.start();
                        break;
                }
            }else {
                if ("COMPPROF".equals(requestType)) {

                    Thread COMPPROF = new completeProfileHandler(socket);
                    COMPPROF.setName("COMPPROF");
                    COMPPROF.start();
                } else if ("PASSWD".equals(requestType)) {
                    Thread PASSWD = new passwdHandler(socket);
                    PASSWD.setName("PASSWD");
                    PASSWD.start();
                }
            }
        } catch (SocketException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
