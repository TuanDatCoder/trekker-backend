package com.tuandatcoder.trekkerbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String username, String verificationLink) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản của bạn");

            String content = "<html>" +
                    "<body>" +
                    "<div style=\"font-family: Arial, sans-serif; margin: 0; padding: 0;\">" +
                    "<div style=\"background-color: #007bff; padding: 20px; text-align: center; color: white;\">" +
                    "<h1>devprojectlabvn</h1>" +
                    "</div>" +
                    "<div style=\"padding: 20px; background-color: #f9f9f9;\">" +
                    "<h2 style=\"color: #333;\">Chào " + username + ",</h2>" +
                    "<p style=\"font-size: 16px; color: #555;\">Cảm ơn bạn đã đăng ký tài khoản tại <strong>devprojectlabvn</strong>. Vui lòng nhấp vào nút bên dưới để xác thực tài khoản của bạn:</p>" +
                    "<div style=\"text-align: center; margin: 20px;\">" +
                    "<a href=\"" + verificationLink + "\" style=\"background-color: #28a745; color: white; padding: 15px 30px; text-decoration: none; font-size: 16px; border-radius: 5px;\">Xác thực tài khoản</a>" +
                    "</div>" +
                    "<p style=\"font-size: 16px; color: #555;\">Nếu bạn không yêu cầu tạo tài khoản này, vui lòng bỏ qua email này.</p>" +
                    "</div>" +
                    "<div style=\"background-color: #007bff; padding: 10px; text-align: center; color: white;\">" +
                    "<p>&copy; 2023 devprojectlabvn. All rights reserved.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
