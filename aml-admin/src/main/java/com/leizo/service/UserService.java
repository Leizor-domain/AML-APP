package com.leizo.service;

import com.leizo.admin.auth.Users;

public interface UserService {
    Users register(Users user);

    Users verify(String username, String rawpassword);

    Users findByUsername(String username);
}
