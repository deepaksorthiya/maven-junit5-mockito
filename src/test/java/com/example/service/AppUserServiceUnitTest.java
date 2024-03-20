package com.example.service;

import com.example.model.AppUser;
import com.example.repository.AppUserRepository;
import com.example.repository.MailClient;
import com.example.repository.SettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppUserServiceUnitTest {

    private AppUserService userService;

    private SettingRepository settingRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private MailClient mailClient;

    private AppUser appUser;

    @BeforeEach
    void init(@Mock SettingRepository settingRepository) {
        userService = new DefaultAppUserService(userRepository, settingRepository, mailClient);

        lenient().when(settingRepository.getUserMinAge()).thenReturn(10);

        when(settingRepository.getUserNameMinLength()).thenReturn(4);

        lenient().when(userRepository.isUsernameAlreadyExists(any(String.class)))
                .thenReturn(false);

        this.settingRepository = settingRepository;
    }

    @Test
    void givenValidUser_whenSaveUser_thenSucceed(@Mock MailClient mailClient) {
        // Given
        appUser = new AppUser("Jerry", 12);
        when(userRepository.insert(any(AppUser.class))).then(new Answer<AppUser>() {
            int sequence = 1;

            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                AppUser user = (AppUser) invocation.getArgument(0);
                user.setId(sequence++);
                return user;
            }
        });

        userService = new DefaultAppUserService(userRepository, settingRepository, mailClient);

        // When
        AppUser insertedUser = userService.register(appUser);

        // Then
        verify(userRepository).insert(appUser);
        assertNotNull(appUser.getId());
        verify(mailClient).sendUserRegistrationMail(insertedUser);
    }

    // additional tests

    @Test
    void givenShortName_whenSaveUser_thenGiveShortUsernameError() {
        // Given
        appUser = new AppUser("tom", 12);

        // When
        try {
            userService.register(appUser);
            fail("Should give an error");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), Errors.USER_NAME_SHORT);
        }

        // Then
        verify(userRepository, never()).insert(appUser);
    }

    @Test
    void givenSmallAge_whenSaveUser_thenGiveYoungUserError() {
        // Given
        appUser = new AppUser("jerry", 3);

        // When
        try {
            userService.register(appUser);
            fail("Should give an error");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), Errors.USER_AGE_YOUNG);
        }
        // Then
        verify(userRepository, never()).insert(appUser);
    }

    @Test
    void givenUserWithExistingName_whenSaveUser_thenGiveUsernameAlreadyExistsError() {
        // Given
        appUser = new AppUser("jerry", 12);
        reset(userRepository);
        when(userRepository.isUsernameAlreadyExists(any(String.class))).thenReturn(true);

        // When
        try {
            userService.register(appUser);
            fail("Should give an error");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), Errors.USER_NAME_DUPLICATE);
        }
        // Then
        verify(userRepository, never()).insert(appUser);
    }

}
