/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import bdd.ProjectsManagement;
import bdd.entities.User;
import bdd.rmi.InvocationContext;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author otmangx
 */
class Connection implements Runnable {
    
    private Socket socketDialogue;
    Server ser;
    ObjectInputStream inobj;
    ObjectOutputStream outobj;
    DataOutputStream out;
    private Map<String, Object> services = new HashMap<String, Object>();
    Map<String, User> req = null;
    
    public Connection(Socket socket, Server ser) throws IOException {
        this.socketDialogue = socket;
        this.ser = ser;
        
        
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run() {

        try {
                User user;
                inobj = new ObjectInputStream(
                        socketDialogue.getInputStream());
                
                out = new DataOutputStream(
                        socketDialogue.getOutputStream());
                
                while (true) {
                    user = (User)inobj.readObject();
                    if(this.ser.user_m.isUser(user)) {
                        if (user.getBlocked()) out.writeBytes("This user is blocked\n");
                        else {
                            if (user.getID()==1) {
                                services.put("UserM", ser.user_m);
                                out.writeBytes("admin\n");
                            } else out.writeBytes("user\n");
                            break;
                        }
                    } else out.writeBytes("The username or the password is incorrect!\n");
                }
                
                services.put("ProjectM", new ProjectsManagement(ser.mydb, user.getID()));
                
                outobj = new ObjectOutputStream(
                        socketDialogue.getOutputStream());
                inobj = new ObjectInputStream(
                        socketDialogue.getInputStream());
                InvocationContext ic ;
                Object targetObject; Object result;
                while (true) {
                    ic = (InvocationContext) inobj.readObject();
                    targetObject = services.get(ic.getName());
                    result = targetObject.getClass().getMethod(ic.getMethod(), args2Class(ic.getArgs())).invoke(targetObject, ic.getArgs());
                    outobj.writeObject(result);
                }}
            catch (EOFException ex) {
                System.out.println(ex.getMessage());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
            ser.i--;
            try{
                inobj.close();
                out.close();
                socketDialogue.close();
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
                
    
    private Class[] args2Class (Object[] objs) {
        List<Class> classes = new ArrayList<>();
        if (objs!=null)
        for (Object o : objs) {
            classes.add(o.getClass());
        }
        return classes.toArray(new Class[]{});
    }
    }