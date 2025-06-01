//package lk.ijse.hotelmanagementsystem.controller;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import lk.ijse.hotelmanagementsystem.dto.UserDTO;
//import lk.ijse.hotelmanagementsystem.model.LogInPageModel;
//
//public class LogInPageController {
//
//    @FXML
//    private PasswordField txtPassword;
//
//    @FXML
//    private TextField txtUserName;
//
//    private LogInPageModel logInPageModel  = new LogInPageModel();
//
//    @FXML
//    void logInAction(ActionEvent event) {
//        String userName = txtUserName.getText();
//        String password = txtPassword.getText();
//
//        if(userName.isEmpty() || password.isEmpty()){
//            return;
//        }
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername(userName);
//        userDTO.setPasswordHash(password);
//
//        boolean result = logInPageModel.cheackUser(userDTO);
//
//        if(!result){
//            return;
//        }
//
//        UserDTO userDetails = logInPageModel.getUserDetails(userDTO);
//    }
//
//
//        if(){
//
//}
//}
