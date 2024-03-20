package com.example.service;


import com.example.model.AppUser;
import com.example.repository.AppUserRepository;
import com.example.repository.MailClient;
import com.example.repository.SettingRepository;

public class DefaultAppUserService implements AppUserService {

    private AppUserRepository appUserRepository;
    private SettingRepository settingRepository;
    private MailClient mailClient;

    public DefaultAppUserService(AppUserRepository appUserRepository, SettingRepository settingRepository, MailClient mailClient) {
        this.appUserRepository = appUserRepository;
        this.settingRepository = settingRepository;
        this.mailClient = mailClient;
    }

    @Override
    public AppUser register(AppUser appUser) {
        validate(appUser);
        AppUser insertedUser = appUserRepository.insert(appUser);
        mailClient.sendUserRegistrationMail(insertedUser);
        return insertedUser;
    }

    private void validate(AppUser appUser) {
        if (appUser.getName() == null) {
            throw new RuntimeException(Errors.USER_NAME_REQUIRED);
        }

        if (appUser.getName()
                .length() < settingRepository.getUserNameMinLength()) {
            throw new RuntimeException(Errors.USER_NAME_SHORT);
        }

        if (appUser.getAge() < settingRepository.getUserMinAge()) {
            throw new RuntimeException(Errors.USER_AGE_YOUNG);
        }

        if (appUserRepository.isUsernameAlreadyExists(appUser.getName())) {
            throw new RuntimeException(Errors.USER_NAME_DUPLICATE);
        }
    }

}
