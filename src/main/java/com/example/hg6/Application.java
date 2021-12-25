package com.example.hg6;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {
        try (OutputStream output = new FileOutputStream("C:\\Documents\\hg6\\target\\config.properties.properties")) {

            Properties prop = new Properties();

            prop.setProperty("db.url", "jdbc:mysql://127.0.0.1:3306/lab5");
            prop.setProperty("db.user", "root");
            prop.setProperty("db.password", "aripaalba");

            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}
