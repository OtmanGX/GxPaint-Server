/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import bdd.entities.Project;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 *
 * @author otmangx
 */
import java.security.NoSuchAlgorithmException;
import java.util.List;
/**
 *
 * @author otmangx
 */
public class Bdd {

    String url;
    String user;
    String passwd;
    Connection conn;

    /**
     * @param args the command line arguments
     */
    public Bdd(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.passwd = password;
        connect();
    }

    public Bdd() {
        this("jdbc:postgresql://localhost:5432/paint",
                "otmangx", "123");
    }

    public Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver O.K.");

            conn = DriverManager.getConnection(url, user, passwd);
            System.out.println("Connection effective !");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public ResultSet execQuery(String query) throws SQLException {
        return this.conn.createStatement().executeQuery(query);
    }
    
    public void  prepareQuery(String query) throws SQLException {
        this.conn.createStatement().executeUpdate(query);
    }

    public int insert(String table, Map values) throws SQLException {

        StringBuilder columns = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        for (Object col : values.keySet()) {
            columns.append(col).append(",");

            if (values.get(col) instanceof String) {
                vals.append("'").append(values.get(col)).append("',");
            } else {
                vals.append(values.get(col)).append(",");
            }
        }
        //System.out.println(columns + "\n" + vals);
        columns.setLength(columns.length() - 1);
        vals.setLength(vals.length() - 1);
        
        String query = String.format("INSERT INTO %s (%s) VALUES (%s);", table,
                columns.toString(), vals.toString());

        return this.conn.createStatement().executeUpdate(query);
    }

    

}
