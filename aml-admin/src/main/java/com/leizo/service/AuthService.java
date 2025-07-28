package com.leizo.service;

import com.leizo.common.entity.Users;

public interface AuthService {
    Users authenticate (String username, String password);
    boolean authorize (Users user, String requiredRole);
}
