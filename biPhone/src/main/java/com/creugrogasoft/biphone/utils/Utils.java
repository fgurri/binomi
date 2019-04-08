package com.creugrogasoft.biphone.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fgurri
 */
public class Utils {
     public static Connection createDataWarehouseConnection() {
        // Create connection to Tesis DB
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/datawarehouse?user=datawarehouse&password=warehouse08");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
             Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
         }
	
	return conn;
    }
     
    public static Connection createAsteriskConnection() {
        // Create connection to Tesis DB
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.2.10:3306/asteriskcdrdb?user=root&password=Puilla!08");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
             Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
         }
	
	return conn;
    }
    
    public static Connection createAsteriskCoreConnection() {
        // Create connection to Tesis DB
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.2.10:3306/asterisk?user=asteriskuser&password=Puilla!08");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
             Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
         }
	
	return conn;
    }
    
    public static String parseSecondsToReport (int seconds) {
        
        int s = seconds % 60;
        int h = seconds / 60;
	int m = h % 60;
	h= h / 60;
        return (String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s));
    }
    
    /*
     * Receives a date in format YYYY-MM-DD and returns DD/MM/YYYY
     */
    public static String invertStringDate (String date) {
        
    	if (date.compareTo("") == 0) 
    		return "";
    	
        String splittedDate[] = date.split("-"); 
        String year = splittedDate[0];
        String month = splittedDate[1];
        String day = splittedDate[2];
        
        return day + "/" + month + "/" + year;
    }
}
