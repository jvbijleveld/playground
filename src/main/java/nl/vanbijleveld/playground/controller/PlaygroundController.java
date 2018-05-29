package nl.vanbijleveld.playground.controller;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import nl.vanbijleveld.imapreader.ImapService;
import nl.vanbijleveld.imapreader.entities.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {

    @Value("${email.smtp.host}")
    private String emailHost;

    @Value("${email.smtp.user}")
    private String emailUser;

    @Value("${email.smtp.password}")
    private String emailPass;

    @RequestMapping(value = "/")
    public String homePage() {
        return "home";
    }

    @RequestMapping(value = "/imap")
    public String emailTester() {
        String ret = "The mail I found..<br><br>";
        try {
            ImapService mailService = new ImapService(emailHost, emailUser, emailPass);
            List<Email> newMessages = mailService.getAllNewEmail();
            for (Email mail : newMessages) {
                ret += mail.getSubject() + " received on " + mail.getReceivedDate() + " from " + mail.getFrom() + "<br>";
                System.out.println("processing mail: " + mail.getSubject() + " received on " + mail.getReceivedDate() + " from " + mail.getFrom().toString());

            }

            return ret;
        } catch (MessagingException | IOException e) {
            return "Error connecting mailbox: " + e.getMessage();
        }
    }

}
