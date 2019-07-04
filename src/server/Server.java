/*
 * To change this license header, choose License Headers inobj Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template inobj the editor.
 */
package server;
import bdd.Bdd;
import bdd.UsersManagement;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author otmangx
 */
public class Server {
    static ServerSocket server ;
    static Socket con;
    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    int i=0;
    Bdd mydb;
    UsersManagement user_m;
    private int port = 40900;
    private boolean stop=true;
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public Server() {
        mydb = new Bdd();
        
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        
        Server ser = new Server();
        ser.manageConnection();

        
    }
    
    public void setArretMarche(boolean stop) {
        this.stop = stop;
        }
    
    public void manageConnection() {
        System.out.println("Listen : "+server.getLocalSocketAddress());
        user_m = new UsersManagement(mydb);
        
        while (true) {
            try{
                Socket clientsocket = server.accept();
                new Connection(clientsocket, this);
                i++;
                if (i == maxClientsCount) {
                    System.out.println("Server too busy. Try later.");
                    clientsocket.close();
                }
                System.out.println("Connection from : "+clientsocket.getRemoteSocketAddress());
                if(!stop){
                    System.out.println("Server stoped. ");
                    server.close();
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


