package org.bank.config;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.io.InputStream;
import java.util.Properties;

public class MailConfig {
    private  static final Properties prop = new Properties();

    private  MailConfig(){}

    static {
        try(InputStream input = MailConfig.class.getClassLoader().
                getResourceAsStream("application.properties")){

            if(input == null)
                System.out.println("Sorry, unable to find application.properties");
            else
                prop.load(input);

        }catch (Exception e){
            throw  new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static Session getSession() {
        boolean authEnabled = Boolean.parseBoolean(prop.getProperty("mail.smtp.auth", "false"));

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", prop.getProperty("mail.smtp.host", "localhost"));
        mailProps.put("mail.smtp.port", prop.getProperty("mail.smtp.port", "2525"));
        mailProps.put("mail.smtp.auth", String.valueOf(authEnabled));
        mailProps.put("mail.smtp.starttls.enable", prop.getProperty("mail.smtp.starttls.enable", "false"));
        mailProps.put("mail.smtp.ssl.enable", prop.getProperty("mail.smtp.ssl.enable", "false"));
        if(authEnabled){
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            prop.getProperty("mail.username"),
                            prop.getProperty("mail.password")
                    );
                }
            };
            return Session.getInstance(mailProps, authenticator);
        }else {
            return Session.getInstance(mailProps);
        }

    }

    public static String getFromName() {
        return prop.getProperty("mail.from.name");
    }

    public static String getUsername() {
        return prop.getProperty("mail.username");
    }

    public static int getMailSendDelayMs() {
        String delay = prop.getProperty("mail.send.delay.ms", "1000"); // default 1s
        try {
            return Integer.parseInt(delay);
        } catch (NumberFormatException e) {
            System.err.println("Invalid mail.send.delay.ms value, using default 1000ms");
            return 1000;
        }
    }

}