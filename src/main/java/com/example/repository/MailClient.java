package com.example.repository;

import com.example.model.AppUser;

public interface MailClient {

    void sendUserRegistrationMail(AppUser appUser);

}