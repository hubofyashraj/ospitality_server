package com.ospitality.server.requestHandlers;

import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static com.ospitality.server.common.getCon;

public class markVisitHandler extends Thread{
    Socket socket;

    public markVisitHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        DataOutputStream dout = null;
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            String str = din.readUTF();
            String[] data = str.split("\\./");

            Statement st = getCon().createStatement();

            ResultSet rs = st.executeQuery("SELECT DESIGNATION FROM HMS WHERE USERID LIKE '"+data[1]+"'");

            if(rs.next()){

                String designation = rs.getString(1);

                st.executeUpdate("UPDATE APPOINTMENTS SET VISITED=1 WHERE PATIENT LIKE '"+data[0]+"' AND DEPARTMENT LIKE '"+designation+"' AND DATE LIKE CURDATE()");

                st.executeUpdate("INSERT INTO VISITED VALUES (CURDATE(),'"+data[0]+"','"+designation+"','"+data[1]+"')");

                rs = st.executeQuery("SELECT AGE,GENDER FROM PATIENTS WHERE PATIENT_ID LIKE '"+data[0]+"'");
                if(rs.next()){
                    int age = rs.getInt(1);
                    String gen = rs.getString(2);
                    String gender= String.format("%sVISITS",gen);
                    String ageGrp= String.format("PATIENTAGE%d_%d", age + 1 - age % 10, age + 10 - age % 10) ;
                    if(age>60)  ageGrp="PATIENTAGE60ABOVE";
                    rs = st.executeQuery(String.format("SELECT * FROM VISITS WHERE DATE LIKE '%s'",
                            LocalDate.now()));
                    if(rs.next()){
                        int genderCount = rs.getInt(gender);
                        genderCount++;
                        int count = rs.getInt(ageGrp);
                        count++;
                        st.executeUpdate(String.format("UPDATE VISITS SET %s='%d',%s='%d' WHERE DATE LIKE '%s'",
                                ageGrp,count,gender,genderCount,LocalDate.now()));
                    }else{
                        st.executeUpdate(String.format("INSERT INTO VISITS (DATE,%s,%s) values ('%s',1,1)",
                                ageGrp,gender,LocalDate.now()));
                    }
                }


                rs = st.executeQuery(String.format("SELECT * FROM EVERYDAYDETAILS WHERE DATE LIKE '%s'",
                        LocalDate.now()));
                if(rs.next()){
                    st.executeUpdate(String.format("UPDATE EVERYDAYDETAILS SET VISITSTODAY='%d'" +
                            " WHERE DATE LIKE '%s'", rs.getInt(4)+1,LocalDate.now()));
                }else{
                    st.executeUpdate(String.format("INSERT INTO EVERYDAYDETAILS VALUES " +
                            "('%s',0,0,1)", LocalDate.now()));
                }

                dout.writeBoolean(true);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            if (dout!=null) {
                try {
                    dout.writeBoolean(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        new HandleRequestThread(socket).start();
    }
}
