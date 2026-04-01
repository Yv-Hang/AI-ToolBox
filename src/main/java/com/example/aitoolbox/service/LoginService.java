package com.example.aitoolbox.service;

import com.example.aitoolbox.entity.User;

public interface LoginService {
    void sendCode(String email);

    void register(User user);
}
