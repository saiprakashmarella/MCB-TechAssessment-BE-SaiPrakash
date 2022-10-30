package com.interview.accounts.security;


import com.interview.accounts.config.SpringSecurityConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@RunWith(MockitoJUnitRunner.class)
public class AccountSecurityTest {

    @InjectMocks
    private AccountSecurity accountSecurity;

    @Mock
    private SpringSecurityConfig springSecurityConfig;

    @Mock
    private JWTUtils jwtUtils;

    @Test
    public void AuthenticateUserTest(){
        Mockito.when(springSecurityConfig.getUserName()).thenReturn("admin");
        Mockito.when(springSecurityConfig.getPassword()).thenReturn("password");
        Assert.assertTrue(accountSecurity.AuthenticateUser("admin","password"));
    }

}

