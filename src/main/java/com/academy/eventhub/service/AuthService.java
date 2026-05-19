package com.academy.eventhub.service;

import com.academy.eventhub.dto.RegisterRequest;
import com.academy.eventhub.entity.User;
import org.springframework.stereotype.Service;


public interface AuthService
{
    public User register (RegisterRequest registerRequest);
}
