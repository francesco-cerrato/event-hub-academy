package com.academy.eventhub.service;

import com.academy.eventhub.entity.User;
import org.springframework.stereotype.Service;


public interface AuthService
{
    public User register (String username, String password);
}
