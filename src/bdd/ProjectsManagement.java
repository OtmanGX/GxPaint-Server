/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import bdd.dao.ProjectsDAO;
import bdd.entities.Project;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author otmangx
 */
public class ProjectsManagement implements ProjectsDAO{
    private int user_id;
    Map values;
    String table ="projects";
    Bdd db;
    MessageDigest md;
    
    public ProjectsManagement(Bdd db, int id) {
        user_id = id ;
        this.db = db;
        values = new HashMap();
    }
    
    @Override
    public void create(Project pr) throws SQLException {
        LocalDate localDate = LocalDate.now();
        java.sql.Date date = java.sql.Date.valueOf(localDate);
        
        PreparedStatement stm = db.conn.prepareStatement("insert into projects(name, date, user_id) values(?, ?, ?);");
        stm.setString(1, pr.getName());
        stm.setDate(2, date);
        stm.setInt(3, user_id);
        stm.executeUpdate();

    }

    @Override
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projectsList = new ArrayList<Project>();
        ResultSet rs = db.execQuery("SELECT * FROM projects;");
		while (rs.next()) {
			Project pr = new Project(rs.getInt(1),
                                            rs.getString(2), 
                                            rs.getDate(3)); 
                        
			projectsList.add(pr);
		}
        return projectsList;
    }
    
    public Project getProjectByKey(String key) throws SQLException {
        String query = String.format("SELECT * FROM projects where hash='%s';", key);
        ResultSet rs = db.execQuery(query);
        rs.next();
        Project pr = new Project(rs.getInt(1),
                                 rs.getString(2), 
                                 rs.getDate(3));
        return pr;
    }
    
    public String getKey(Project pr) throws SQLException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String key = md.digest(String.valueOf(pr.getId()).getBytes()).toString(); 
        String query = String.format("UPDATE projects set hash='%s' where id=%d;"
                                    ,key, pr.getId());
        db.prepareQuery(query);
        return key;
    }
    
    @Override
    public List<Project> getAllProjectsByUserId() throws SQLException {
        if (user_id==1) return getAllProjects();
        List<Project> projectsList = new ArrayList<Project>();
        String query = String.format("SELECT * FROM projects where user_id=%d;", user_id);
        ResultSet rs = db.execQuery(query);
		while (rs.next()) {
			Project pr = new Project(rs.getInt(1),
                                            rs.getString(2), 
                                            rs.getDate(3)); 
                        
			projectsList.add(pr);
		}
        return projectsList;
    }

    @Override
    public void update(Project pr) throws SQLException {
        String query = String.format("UPDATE projects set name='%s' where id=%d;"
                                    ,pr.getName(), pr.getId());
        db.prepareQuery(query);
    }
    
    public synchronized void setData(Project pr) throws IOException, SQLException {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(pr.getData());
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        PreparedStatement stm = db.conn.prepareStatement("update projects set data=? where id=?;");
        stm.setInt(2, pr.getId());
        stm.setBinaryStream(1, bais);
        stm.executeUpdate();
    }
    
    public synchronized Vector getData(Integer id) throws SQLException, IOException, ClassNotFoundException {
        String query = String.format("select * from projects where id=%d;", id);
        ResultSet res = db.execQuery(query);
        res.next();
        ByteArrayInputStream bais=(ByteArrayInputStream) res.getBinaryStream(4);
        if (bais!=null) {
            ObjectInputStream in = new ObjectInputStream(bais);
            return (Vector)in.readObject();
        }else return null;
        
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String query = String.format("delete from projects where id=%d;",id);
         db.prepareQuery(query);
    }

    
    
}
