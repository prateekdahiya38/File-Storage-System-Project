package com.example.filesystem.mappers;

import com.example.filesystem.dtos.responseDtos.UserDetailDto;
import com.example.filesystem.models.User;

public class UserMapper {

    public static UserDetailDto userToUserDetailDtoConversion(User user){
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setName(user.getName());
        userDetailDto.setEmail(user.getEmail());
        userDetailDto.setUserId(user.getId());
        return userDetailDto;
    }
}
