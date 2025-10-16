package org.bank.serviceImpl;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.bank.config.MailConfig;
import org.bank.service.NotificationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements NotificationService {
    private  final  Session session;
    private final  String fromUsername;
    private final String fromName;

    public NotificationServiceImpl(){
        session = MailConfig.getSession();
        fromUsername = MailConfig.getUsername();
        fromName = MailConfig.getFromName();
    }

    @Override
    public void emailAlert(String senderEmail, String senderName,
                           String receiverEmail, String receiverName,
                           String senderAccount, String receiverAccount, double amount) {

        String dataTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        //Sender Body
        String senderBody = loadTemplate("mail-templates/sender-transaction.html")
                .replace("{customer}", senderName)
                .replace("{senderAccount}", senderAccount)
                .replace("{receiverAccount}", receiverAccount)
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{date}", dataTime);

        //Sender Subject
        String senderSubject = loadTemplate("mail-templates/sender-subject.txt")
                .replace("{senderAccount}", senderAccount)
                .replace("{amount}", String.format("%.2f", amount));

        sendMail(senderEmail, senderSubject, senderBody);

        //Receiver Body
        String receiverBody = loadTemplate("mail-templates/receiver-transaction.html")
                .replace("{customer}", senderName)
                .replace("{senderAccount}", senderAccount)
                .replace("{receiverAccount}", receiverAccount)
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{date}", dataTime);

        //Receiver Subject
        String receiverSubject = loadTemplate("mail-templates/receiver-subject.txt")
                .replace("{receiverAccount}", receiverAccount)
                .replace("{amount}", String.format("%.2f", amount));

        sendMail(receiverEmail, receiverSubject, receiverBody);
    }


    private  void sendMail(String mail, String subject, String body){
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.fromUsername, this.fromName));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Email sent successfully to " + mail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + mail + " : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private  String loadTemplate(String path){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8))){

            return br.lines().collect(Collectors.joining("\n"));
        }catch (Exception e){
            System.err.println("Error loading template: " + path + " -> " + e.getMessage());
            return "";
        }
    }
}
