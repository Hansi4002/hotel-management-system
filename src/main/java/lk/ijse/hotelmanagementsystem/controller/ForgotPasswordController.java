package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class ForgotPasswordController {
    public TextField txtEmail;
    public Button btnSend;
    public ProgressIndicator progressIndicator;
    public Label lblMessage;

    private final String from = "paboda4002@gmail.com";
    private final String password = "buub wgvh fije rqfu";
    private final String subject = "Reset Your Password";

    public void backToLoginOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSend.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Management System - Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading login page.", ButtonType.OK).show();
        }
    }

    public void sendResetLinkOnAction(ActionEvent actionEvent) {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            lblMessage.setText("Please enter an email address");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            lblMessage.setText("Invalid email address");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        btnSend.setDisable(true);
        progressIndicator.setVisible(true);
        lblMessage.setText("");

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText("Click this link to reset your password: http://your-app.com/reset?token=xyz");

            lblMessage.setText("Reset link sent!");
            lblMessage.setStyle("-fx-text-fill: green;");
            new Alert(Alert.AlertType.INFORMATION, "Email sent successfully!").show();
        } catch (MessagingException e) {
            lblMessage.setText("Failed to send email");
            lblMessage.setStyle("-fx-text-fill: red;");
            new Alert(Alert.AlertType.ERROR, "Failed to send email: Invalid credentials").show();
        } finally {
            btnSend.setDisable(false);
            progressIndicator.setVisible(false);
        }
    }
}
