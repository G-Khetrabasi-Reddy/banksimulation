package org.bank;

import org.bank.config.DBSetup;


public class App {
    public static  void  main(String[] args){
        try{
            DBSetup.addDefaultAdmin();
            System.out.println("default admin added successfully.");
        }catch (RuntimeException e){
            System.err.println("Error: Database setup failed.");
            e.printStackTrace();
        }
    }
}