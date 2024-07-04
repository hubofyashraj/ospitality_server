package com.ospitality.server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;
import static java.util.Objects.requireNonNull;

public class Login {

    @FXML
    public CheckBox saveDetails;
    @FXML
    public Button loginBtn;

    @FXML
    private TextField userID;

    @FXML
    private PasswordField password;

    @FXML
    private Label txt;
    @FXML
    private ImageView closeBtn;

    File file = new File(common.getWorkingDirectory() + "db");

    @FXML
    public void initialize(){
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                userID.setText(reader.readLine());
                password.setText(reader.readLine());
                saveDetails.setSelected(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void checkLogin() throws ClassNotFoundException {
        String uName = userID.getText();
        String uPass = password.getText();


        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con;
        DatabaseMetaData metaData = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306",uName,uPass);

            metaData = con.getMetaData();

            if(metaData!=null){

                common.setUName(uName);
                common.setUPass(uPass);

                if(saveDetails.isSelected()){
                    PrintWriter writer = new PrintWriter(new FileOutputStream(file));
                    writer.println(uName);
                    writer.println(uPass);
                    writer.close();
                }else{
                    if(file.exists()){
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }

                ResultSet rs = metaData.getCatalogs();
                boolean checkDB = false;
                while(rs.next()){
                    if(rs.getString(1).equals("ospitality")||rs.getString(1).equals("OSPITALITY"))    checkDB=true;
                }

                if(!checkDB){
                    createScript();
                    Thread.sleep(1000);
                    ScriptRunner sr = new ScriptRunner(con);
                    sr.runScript(new InputStreamReader(new FileInputStream(common.getWorkingDirectory()+"script.sql")));

                }

                Parent root = FXMLLoader.load(requireNonNull(getClass().getResource("sample.fxml")));
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                WindowStyle.allowDrag(root, common.primaryStage);
                stage.setScene(new Scene(root));
            }
        } catch (SQLException | IOException e) {
            if(metaData==null){
                txt.setText("Please Enter Correct Credentials");
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void mouseEntered() throws URISyntaxException {
        closeBtn.setImage(new Image(requireNonNull(getClass().getResource("/assets/cancelRed.png")).toURI().toString()));
    }
    public void mouseExited() throws URISyntaxException {
        closeBtn.setImage(new Image(requireNonNull(getClass().getResource("/assets/cancelDark.png")).toURI().toString()));
    }

    public void closeWindow() {
        ((Stage)loginBtn.getScene().getWindow()).close();
    }




    private void createScript() throws IOException, InterruptedException {
        String script = """
            CREATE DATABASE IF NOT EXISTS OSPITALITY;
            USE OSPITALITY;
            CREATE TABLE IF NOT EXISTS EVERYDAYDETAILS
            (
              DATE              DATE          NOT NULL,
              TOTALAPPOINTMENTS INT DEFAULT 0 NOT NULL,
              NEWPATIENTS       INT DEFAULT 0 NOT NULL,
              VISITSTODAY       INT DEFAULT 0 NOT NULL,
              CONSTRAINT EVERYDAYDETAILS_PK
                  UNIQUE (DATE)
            );
          
            CREATE TABLE IF NOT EXISTS HMS
            (
              USERNAME        VARCHAR(25)  DEFAULT 'NULL' NOT NULL,
              USERID          VARCHAR(30)                 NOT NULL,
              NUMERICID       INT                         NOT NULL,
              PROFILECOMPLETE TINYINT(1)   DEFAULT 0      NOT NULL,
              PASSWORD        VARCHAR(45)  DEFAULT 'AA'   NULL,
              GENDER          VARCHAR(10)  DEFAULT 'NULL' NULL,
              ROLE            VARCHAR(30)  DEFAULT 'NULL' NOT NULL,
              DESIGNATION     VARCHAR(30)  DEFAULT 'NULL' NOT NULL,
              MOBILENUMBER    BIGINT                      NULL,
              WORKEMAIL       VARCHAR(45)  DEFAULT 'NULL' NULL,
              ADDRESS         VARCHAR(255) DEFAULT 'NULL' NULL,
              PERSONALEMAIL   VARCHAR(45)  DEFAULT 'NULL' NULL,
              JOINING         VARCHAR(15)  DEFAULT 'NULL' NOT NULL,
              CONSTRAINT HMS_USERID_UINDEX
                  UNIQUE (USERID)
            );
          
            CREATE TABLE IF NOT EXISTS DOCTORS
            (
              ID         VARCHAR(10) NOT NULL,
              DEPARTMENT VARCHAR(30) NOT NULL,
              CONSTRAINT DOCTORS_DEPARTMENT_UINDEX
                  UNIQUE (DEPARTMENT),
              CONSTRAINT DOCTORS_ID_UINDEX
                  UNIQUE (ID),
              CONSTRAINT DOCTORS_HMS_USERID_FK
                  FOREIGN KEY (ID) REFERENCES HMS (USERID)
            );
          
            CREATE TABLE IF NOT EXISTS LABTESTS
            (
              ASSIGNMENTDATE DATE         NOT NULL,
              PATIENT        VARCHAR(10)  NOT NULL,
              TESTNAME       VARCHAR(255) NOT NULL,
              TESTDONEON     DATE         NULL
            );
          
            CREATE TABLE IF NOT EXISTS MEDICINES
            (
              `MEDICINE NAME`       TEXT NULL,
              PRESCRIPTION          TEXT NULL,
              `TYPE OF SELL`        TEXT NULL,
              MANUFACTURER          TEXT NULL,
              SALT                  TEXT NULL,
              MRP                   TEXT NULL,
              USES                  TEXT NULL,
              `ALTERNATE MEDICINES` TEXT NULL,
              `SIDE EFFECTS`        TEXT NULL,
              `HOW TO USE`          TEXT NULL,
              `CHEMICAL CLASS`      TEXT NULL,
              `HABIT FORMING`       TEXT NULL,
              `THERAPEUTIC CLASS`   TEXT NULL,
              `ACTION CLASS`        TEXT NULL
            );
          
            CREATE TABLE IF NOT EXISTS PASSWORDREQUESTS
            (
              USERID VARCHAR(10) NULL,
              CONSTRAINT PASSWORDREQUESTS_HMS_USERID_FK
                  FOREIGN KEY (USERID) REFERENCES HMS (USERID)
            );
          
            CREATE TABLE IF NOT EXISTS PATIENTS
            (
              NAME           TEXT                          NOT NULL,
              AGE            INT                           NOT NULL,
              DOB            TEXT                          NOT NULL,
              GENDER         ENUM ('MALE', 'FEMALE')       NOT NULL,
              LAST_DIAGNOSED TEXT                          NULL,
              PATIENT_ID     VARCHAR(10)                   NOT NULL,
              NUMERIC_ID     INT                           NULL,
              REMARKS        LONGTEXT DEFAULT 'NO REMARKS' NOT NULL,
              CONSTRAINT PATIENTS_PATIENT_ID_UINDEX
                  UNIQUE (PATIENT_ID)
            );
          
            CREATE TABLE IF NOT EXISTS APPOINTMENTS
            (
              DATE       DATE                    NOT NULL,
              PATIENT    VARCHAR(10)             NOT NULL,
              DEPARTMENT TEXT                    NOT NULL,
              GENDER     ENUM ('MALE', 'FEMALE') NOT NULL,
              VISITED    TINYINT(1) DEFAULT 0    NOT NULL,
              CONSTRAINT APPOINTMENTS_PATIENTS_PATIENT_ID_FK
                  FOREIGN KEY (PATIENT) REFERENCES PATIENTS (PATIENT_ID)
            );
          
            CREATE TABLE IF NOT EXISTS PROFILE_PICS
            (
              USER_ID     VARCHAR(10)          NOT NULL,
              IS_UPLOADED TINYINT(1) DEFAULT 0 NOT NULL,
              CONSTRAINT PROFILE_PICS_USER_ID_UINDEX
                  UNIQUE (USER_ID),
              CONSTRAINT PROFILE_PICS_HMS_USERID_FK
                  FOREIGN KEY (USER_ID) REFERENCES HMS (USERID)
            );
          
            CREATE TABLE IF NOT EXISTS SUSPENDED_STAFF
            (
              ID                VARCHAR(30)  NOT NULL,
              SUSPENSION_REASON VARCHAR(255) NULL,
              SUSPENSION_DATE   DATE         NULL,
              CONSTRAINT SUSPENDED_STAFF_ID_UINDEX
                  UNIQUE (ID),
              CONSTRAINT SUSPENDED_STAFF_HMS_USERID_FK
                  FOREIGN KEY (ID) REFERENCES HMS (USERID)
            );
          
            CREATE TABLE IF NOT EXISTS VISITED
            (
              DATE       DATE        NOT NULL,
              PATIENT    VARCHAR(10) NOT NULL,
              DEPARTMENT VARCHAR(30) NOT NULL,
              DOCTOR     VARCHAR(30) NOT NULL,
              CONSTRAINT VISITED_HMS_USERID_FK
                  FOREIGN KEY (DOCTOR) REFERENCES HMS (USERID),
              CONSTRAINT VISITED_PATIENTS_PATIENT_ID_FK
                  FOREIGN KEY (PATIENT) REFERENCES PATIENTS (PATIENT_ID)
            );
          
            CREATE TABLE IF NOT EXISTS VISITS
            (
              DATE              DATE          NULL,
              MALEVISITS        INT DEFAULT 0 NULL,
              FEMALEVISITS      INT DEFAULT 0 NULL,
              PATIENTAGE1_10    INT DEFAULT 0 NULL,
              `PATIENTAGE11-20` INT DEFAULT 0 NULL,
              PATIENTAGE21_30   INT DEFAULT 0 NULL,
              PATIENTAGE31_40   INT DEFAULT 0 NULL,
              PATIENTAGE41_50   INT DEFAULT 0 NULL,
              PATIENTAGE51_60   INT DEFAULT 0 NULL,
              PATIENTAGE60ABOVE INT DEFAULT 0 NULL,
              CONSTRAINT VISITS_EVERYDAYDETAILS_DATE_FK
                  FOREIGN KEY (DATE) REFERENCES EVERYDAYDETAILS (DATE)
            );
              
            INSERT INTO HMS (USERNAME,USERID,NUMERICID,PROFILECOMPLETE,PASSWORD,ROLE) VALUES ('ADMIN','ADM1001',1001,0,'AA','ADMIN');
            
            INSERT INTO PROFILE_PICS VALUES ('ADM1001',0);
            """;
        new Logger(script,"script.sql").start();
    }
}
