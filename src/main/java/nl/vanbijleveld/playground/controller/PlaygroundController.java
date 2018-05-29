package nl.vanbijleveld.playground.controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.mail.MessagingException;

import nl.vanbijleveld.imapreader.ImapService;
import nl.vanbijleveld.imapreader.entities.Attachment;
import nl.vanbijleveld.imapreader.entities.Email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

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

        LOGGER.debug("Start fetching new mail");
        try {
            ImapService mailService = new ImapService(emailHost, emailUser, emailPass);
            LOGGER.debug("Connected to host {} with user {}", emailHost, emailUser);
            List<Email> newMessages = mailService.getAllNewEmail();
            for (Email mail : newMessages) {

                LOGGER.debug("Found new email from {} received on {}", mail.getPrettyFrom(), mail.getReceivedDate());
                ret += mail.getSubject() + " received on " + mail.getReceivedDate() + " from " + mail.getPrettyFrom() + "<br>";

                if (mail.getAttachments() != null) {
                    for (Attachment attachment : mail.getAttachments()) {
                        ret += "............" + attachment.getFileName() + "<br>";
                        LOGGER.debug("Found attachment {}", attachment.getFileName());
                    }
                }
                ret += "<br>";

            }

            return ret;
        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to process email", e);
            return "Error connecting mailbox: " + e.getMessage();
        }
    }
}
