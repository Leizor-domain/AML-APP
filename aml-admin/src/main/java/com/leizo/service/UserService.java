package com.leizo.service;

import com.leizo.common.entity.Users;

public interface UserService {
    Users register(Users user);

    Users verify(String username, String rawpassword);

    Users findByUsername(String username);
}
