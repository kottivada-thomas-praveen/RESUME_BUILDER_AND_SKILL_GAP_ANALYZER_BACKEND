package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.ChangePasswordRequest;
import com.thomas.rgsga.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void changePassword(String email, ChangePasswordRequest request);
}
