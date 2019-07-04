/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd.rmi.client;


/**
 *
 * @author otmangx
 */
public class Client {
    public static void main(String[] argv) throws Exception {
        Registry r = new Registry().connect("localhost", 10000);
        AddInterface cs = r.get("Add", AddInterface.class);
        System.out.println(cs.add(2, 3));
        System.out.println(cs.add(2, 77));
    }
}
