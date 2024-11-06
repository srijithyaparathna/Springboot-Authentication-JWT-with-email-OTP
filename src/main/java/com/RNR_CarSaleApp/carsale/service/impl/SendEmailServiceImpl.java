package com.RNR_CarSaleApp.carsale.service.impl;

import com.RNR_CarSaleApp.carsale.model.EmailDetails;
import com.RNR_CarSaleApp.carsale.model.EmailInfo;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Service
public class SendEmailServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(SendEmailServiceImpl.class);

    @Value("${key}")  // Ensure you're using the correct key name in your properties file
    private String key;

    // Method to send a generic email
    public String sendEmail(EmailDetails emailDetails) throws IOException {
        // Prepare sender email info
        EmailInfo fromInfo = emailDetails.getFromAddress();
        Email fromEmail = setEmail(fromInfo.getName(), fromInfo.getEmailAddress());

        // Prepare recipient email info
        EmailInfo toInfo = emailDetails.getToAddress();
        Email toEmail = setEmail(toInfo.getName(), toInfo.getEmailAddress());

        // Create email content
        Content content = new Content("text/plain", emailDetails.getEmailBody());

        // Prepare the email object
        Mail mail = new Mail(fromEmail, emailDetails.getSubject(), toEmail, content);

        // Set up the SendGrid client
        SendGrid sendGrid = new SendGrid(key);

        // Prepare the request to SendGrid's API
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        // Send the request and get the response
        Response response = sendGrid.api(request);

        // Log response status and body
        logger.info("SendGrid Response Status Code: {}", response.getStatusCode());
        logger.info("SendGrid Response Body: {}", response.getBody());

        return response.getBody();  // Return the response body (e.g., success or error message)
    }

    // Helper method to create an Email object
    private Email setEmail(String name, String emailAddress) {
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setName(name);
        return email;
    }

    // Method to send OTP email
    public String sendOTPEmail(String email, String otp) throws IOException {
        // Prepare email details for OTP
        EmailDetails emailDetails = new EmailDetails();
        EmailInfo fromInfo = new EmailInfo("abc", "prasanja96@gmail.com"); // Sender's name and email
        EmailInfo toInfo = new EmailInfo("User", email); // Recipient email is passed from registration
        String subject = "Your OTP for Registration"; // Email subject
        String body = "Your OTP is: " + otp + "\nPlease use this OTP to complete your registration."; // Email body

        // Set the email details
        emailDetails.setFromAddress(fromInfo);
        emailDetails.setToAddress(toInfo);
        emailDetails.setSubject(subject);
        emailDetails.setEmailBody(body);

        // Send the email and return the result
        return sendEmail(emailDetails);
    }
}
