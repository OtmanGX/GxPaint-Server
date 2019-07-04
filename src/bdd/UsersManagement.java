/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import bdd.dao.UsersDAO;
import bdd.entities.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author otmangx
 */
public class UsersManagement implements UsersDAO{
    Bdd db;
    Map values;
    String table ="users";
    
    public UsersManagement(Bdd db) {
        this.db = db;
        values = new HashMap();
    }
    
    
    @Override
    public void create(User user) throws SQLException{
        
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("blocked", user.getBlocked());
        
        db.insert(table, values);
    }
    
    public void update(User user) throws SQLException{
        String query = String.format("UPDATE users set username='%s', password='%s', blocked=%b where id=%d;"
                                    ,user.getUsername(), user.getPassword(),user.getBlocked(), user.getID());
        db.prepareQuery(query);
    }
    
    public void delete(Integer id) throws SQLException{
         String query = String.format("delete from users where id=%d;",id);
         db.prepareQuery(query);
    }

    @Override
    public boolean isUser(User user) throws SQLException{
       
        String query = String.format("SELECT * FROM users where username = '%s' and password = '%s';"
                                    ,user.getUsername(), user.getPassword());
        ResultSet rs = db.execQuery(query);
        rs.next();
        if(rs.getRow()==1) {
            user.setBlocked(rs.getBoolean(4));
            user.setID(rs.getInt(1));
            return true;
        }
        else return false;
    }

    @Override
    public List<User> getAllUsers() throws SQLException{
        List<User> usersList = new ArrayList<User>();
        ResultSet rs = db.execQuery("SELECT * FROM users;");
		while (rs.next()) {
			User user = new User(rs.getString(2), //username
                                            rs.getString(3), //password
                                            rs.getBoolean(4)); //blocked
                        user.setID(rs.getInt(1));
			usersList.add(user);
		}
        return usersList;
    }
    
}
