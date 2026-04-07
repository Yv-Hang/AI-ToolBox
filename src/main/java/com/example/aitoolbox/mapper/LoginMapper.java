package com.example.aitoolbox.mapper;

import com.example.aitoolbox.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    void addUser(User user);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    User selectById(Long id);
}
