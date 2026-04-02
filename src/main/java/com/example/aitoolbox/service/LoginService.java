package com.example.aitoolbox.service;

import com.example.aitoolbox.entity.User;

public interface LoginService {
    void sendRegisterCode(String email);
    
    void sendLoginCode(String email);

    void register(User user);
    
    User login(String usernameOrEmail, String password, String code);
}
