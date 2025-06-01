package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.dto.UserDTO;

public class LogInPageModel {

    public boolean cheackUser(UserDTO userDTO) {
        //Select username from user where username = 'userDTO.getUsername()'

        return true;
    }

    public UserDTO getUserDetails(UserDTO userDTO) {
        //select * from user were  username = 'userDTO.getUsername()'

        return userDTO;
    }
}
