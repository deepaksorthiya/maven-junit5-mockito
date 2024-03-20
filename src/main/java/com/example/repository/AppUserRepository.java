package com.example.repository;

import com.example.model.AppUser;

public interface AppUserRepository {

    AppUser insert(AppUser appUser);

    boolean isUsernameAlreadyExists(String userName);
}
