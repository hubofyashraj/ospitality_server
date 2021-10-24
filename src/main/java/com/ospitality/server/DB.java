package com.ospitality.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public Connection getConnect() throws SQLException, ClassNotFoundException {
        Connection con;
        String uName,uPass;

        uName = common.getUName();
        uPass = common.getUPass();

        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OSPITALITY",uName,uPass);

        return con;
    }
}
