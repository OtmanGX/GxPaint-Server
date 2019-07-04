/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd.rmi;

//import sun.plugin.dom.exception.InvalidStateException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Alain Defrance
 */
public class Registry {
    private Map<String, Object> services = new HashMap<String, Object>();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ServerSocket ss;

    public Registry register(String name, Object service) {
        services.put(name, service);
        return this;
    }

    public void publish(int port) throws Exception {
        try {
            ss = new ServerSocket(port);
            while(true) {
                final Socket s = ss.accept();
                new Thread() {
                    public void run() {   
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                            InvocationContext ic ;
                            Object targetObject; Object result;
                            while (true) {
                            ic = (InvocationContext) ois.readObject();
                            targetObject = services.get(ic.getName());
                            result = targetObject.getClass().getMethod(ic.getMethod(), args2Class(ic.getArgs())).invoke(targetObject, ic.getArgs());
                            oos.writeObject(result);}}
                         catch (Exception e) {
                            throw new RMIInvocationException(e.getMessage(), e);
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            throw new RMIInvocationException(e.getMessage(), e);
        }
    }

    private Class[] args2Class (Object[] objs) {
        List<Class> classes = new ArrayList<Class>();
        for (Object o : objs) {
            classes.add(o.getClass());
        }
        return classes.toArray(new Class[]{});
    }
}