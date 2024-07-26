package com.mike.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithTemplate(String to, String brand, String couponCode, String offer) {
        try {
            // Load HTML template from classpath
            Resource resource = getTemplateResource(brand);
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            // Replace placeholders in the template
            String emailBody = htmlTemplate
                    .replace("{{BRAND}}", brand)
                    .replace("{{COUPONCODE}}", couponCode)
                    .replace("{{OFFER}}", offer);

            // Create the email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(to);
            messageHelper.setSubject("Exclusive Offer for " + brand);
            messageHelper.setText(emailBody, true); // true indicates HTML content

            // Send the email
            mailSender.send(mimeMessage);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
    }

    private Resource getTemplateResource(String brand) {
        switch (brand.toLowerCase()) {
            case "sainsbury":
                return new ClassPathResource("Templates/Sainsbury.html");
            case "netflix":
                return new ClassPathResource("Templates/Netflix.html");
            case "nivea":
                return new ClassPathResource("Templates/Nivea.html");
            case "optimumnutrition":
                return new ClassPathResource("Templates/OptimumNutrition.html");
            case "lyft":
                return new ClassPathResource("Templates/Lyft.html");
            case "ikea":
                return new ClassPathResource("Templates/Ikea.html");
            case "ubereats":
                return new ClassPathResource("Templates/UberEats.html");
            case "hamleys":
                return new ClassPathResource("Templates/Hamleys.html");
            case "adidas":
                return new ClassPathResource("Templates/Adidas.html");
            case "stagecoach":
                return new ClassPathResource("Templates/StageCoach.html");

            default:
                throw new IllegalArgumentException("No template found for brand: " + brand);
        }
    }
}