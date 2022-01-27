create or replace database OSPITALITY;

use OSPITALITY;

create or replace table everydaydetails
(
    date              date          not null,
    totalAppointments int default 0 not null,
    newPatients       int default 0 not null,
    visitsToday       int default 0 not null,
    constraint everydayDetails_pk
    unique (date)
);

create or replace table hms
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

create or replace table doctors
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

create or replace table labtests
(
    assignmentDate date         not null,
    patient        varchar(10)  not null,
    testName       varchar(255) not null,
    testDoneOn     date         null
);

create or replace table medicines
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

create or replace table passwordrequests
(
    userID varchar(10) null,
    constraint passwordRequests_HMS_UserID_fk
    foreign key (userID) references hms (UserID)
);

create or replace table patients
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

create or replace table appointments
(
    date       date                    not null,
    patient    varchar(10)             not null,
    department text                    not null,
    gender     enum ('male', 'female') not null,
    visited    tinyint(1) default 0    not null,
    constraint appointments_patients_patient_id_fk
    foreign key (patient) references patients (patient_id)
);

create or replace table profile_pics
(
    user_id     varchar(10)          not null,
    is_uploaded tinyint(1) default 0 not null,
    constraint profile_pics_user_id_uindex
    unique (user_id),
    constraint profile_pics_HMS_UserID_fk
    foreign key (user_id) references hms (UserID)
);

create or replace table suspended_staff
(
    ID                varchar(30)  not null,
    suspension_reason varchar(255) null,
    suspension_date   date         null,
    constraint suspended_staff_ID_uindex
    unique (ID),
    constraint suspended_staff_hms_UserID_fk
    foreign key (ID) references hms (UserID)
);

create or replace table visited
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

create or replace table visits
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

