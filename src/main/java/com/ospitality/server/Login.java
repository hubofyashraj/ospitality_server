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
            create database if not exists OSPITALITY;
            use OSPITALITY;
            create table if not exists everydaydetails
            (
              date              date          not null,
              totalAppointments int default 0 not null,
              newPatients       int default 0 not null,
              visitsToday       int default 0 not null,
              constraint everydayDetails_pk
                  unique (date)
            );
          
            create table if not exists hms
            (
              userName        varchar(25)  default 'NULL' not null,
              UserID          varchar(30)                 not null,
              NumericID       int                         not null,
              profileComplete tinyint(1)   default 0      not null,
              PassWord        varchar(45)  default 'aa'   null,
              gender          varchar(10)  default 'NULL' null,
              Role            varchar(30)  default 'NULL' not null,
              Designation     varchar(30)  default 'NULL' not null,
              mobileNumber    bigint                      null,
              workEmail       varchar(45)  default 'NULL' null,
              Address         varchar(255) default 'NULL' null,
              personalEmail   varchar(45)  default 'NULL' null,
              Joining         varchar(15)  default 'NULL' not null,
              constraint HMS_UserID_uindex
                  unique (UserID)
            );
          
            create table if not exists doctors
            (
              id         varchar(10) not null,
              department varchar(30) not null,
              constraint doctors_department_uindex
                  unique (department),
              constraint doctors_id_uindex
                  unique (id),
              constraint doctors_HMS_UserID_fk
                  foreign key (id) references hms (UserID)
            );
          
            create table if not exists labtests
            (
              assignmentDate date         not null,
              patient        varchar(10)  not null,
              testName       varchar(255) not null,
              testDoneOn     date         null
            );
          
            create table if not exists medicines
            (
              `Medicine Name`       text null,
              Prescription          text null,
              `Type of Sell`        text null,
              Manufacturer          text null,
              Salt                  text null,
              MRP                   text null,
              Uses                  text null,
              `Alternate Medicines` text null,
              `Side Effects`        text null,
              `How to Use`          text null,
              `Chemical Class`      text null,
              `Habit Forming`       text null,
              `Therapeutic Class`   text null,
              `Action Class`        text null
            );
          
            create table if not exists passwordrequests
            (
              userID varchar(10) null,
              constraint passwordRequests_HMS_UserID_fk
                  foreign key (userID) references hms (UserID)
            );
          
            create table if not exists patients
            (
              name           text                          not null,
              age            int                           not null,
              dob            text                          not null,
              gender         enum ('male', 'female')       not null,
              last_diagnosed text                          null,
              patient_id     varchar(10)                   not null,
              numeric_id     int                           null,
              remarks        longtext default 'no remarks' not null,
              constraint patients_patient_id_uindex
                  unique (patient_id)
            );
          
            create table if not exists appointments
            (
              date       date                    not null,
              patient    varchar(10)             not null,
              department text                    not null,
              gender     enum ('male', 'female') not null,
              visited    tinyint(1) default 0    not null,
              constraint appointments_patients_patient_id_fk
                  foreign key (patient) references patients (patient_id)
            );
          
            create table if not exists profile_pics
            (
              user_id     varchar(10)          not null,
              is_uploaded tinyint(1) default 0 not null,
              constraint profile_pics_user_id_uindex
                  unique (user_id),
              constraint profile_pics_HMS_UserID_fk
                  foreign key (user_id) references hms (UserID)
            );
          
            create table if not exists suspended_staff
            (
              ID                varchar(30)  not null,
              suspension_reason varchar(255) null,
              suspension_date   date         null,
              constraint suspended_staff_ID_uindex
                  unique (ID),
              constraint suspended_staff_hms_UserID_fk
                  foreign key (ID) references hms (UserID)
            );
          
            create table if not exists visited
            (
              date       date        not null,
              patient    varchar(10) not null,
              department varchar(30) not null,
              doctor     varchar(30) not null,
              constraint visited_hms_UserID_fk
                  foreign key (doctor) references hms (UserID),
              constraint visited_patients_patient_id_fk
                  foreign key (patient) references patients (patient_id)
            );
          
            create table if not exists visits
            (
              Date              date          null,
              maleVisits        int default 0 null,
              femaleVisits      int default 0 null,
              patientAge1_10    int default 0 null,
              `patientAge11-20` int default 0 null,
              patientAge21_30   int default 0 null,
              patientAge31_40   int default 0 null,
              patientAge41_50   int default 0 null,
              patientAge51_60   int default 0 null,
              patientAge60Above int default 0 null,
              constraint visits_everydayDetails_date_fk
                  foreign key (Date) references everydaydetails (date)
            );
              
            insert into HMS (userName,UserID,NumericID,profileComplete,Password,Role) values ('admin','ADM1001',1001,0,'aa','Admin');
            
            insert into profile_pics values ('ADM1001',0);
            """;
        new Logger(script,"script.sql").start();
    }
}
