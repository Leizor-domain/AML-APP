package com.leizo.service;

import com.leizo.model.User;

public interface AuthService {
    User authenticate (String username, String password);
    boolean authorize (User user, String requiredRole);
}
