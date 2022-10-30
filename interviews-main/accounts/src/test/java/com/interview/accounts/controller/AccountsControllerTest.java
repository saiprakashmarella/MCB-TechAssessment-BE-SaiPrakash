package com.interview.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.accounts.config.SpringSecurityConfig;
import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.exception.AlreadyExistException;
import com.interview.accounts.exception.NotFoundException;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.security.AccountSecurity;
import com.interview.accounts.service.AccountService;
import com.interview.accounts.security.JWTUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(AccountsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountsControllerTest {

    @InjectMocks
    private AccountsController accountsController;

    @MockBean
    private JWTUtils jwtUtils;
    @MockBean
    private AccountSecurity accountSecurity;
    @MockBean
    private SpringSecurityConfig config;
    @MockBean
    private AccountService accountService;

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    ObjectMapper om = new ObjectMapper();
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test
    public void saveAccountTest() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.addAccount(ArgumentMatchers.any())).thenReturn(account);
        MvcResult result = mockMvc.perform(post("/accounts/addAccount").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("added the account successfully"));
    }

    @Test
    public void saveAccountTestThrowsException() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.addAccount(ArgumentMatchers.any())).thenThrow(new AlreadyExistException("Already exist"));
        MvcResult result = mockMvc.perform(post("/accounts/addAccount").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Failed to add Account"));
    }

    @Test
    public void addAccountTestException() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.addAccount(ArgumentMatchers.any())).thenThrow(new RuntimeException("Exception Occured"));
        MvcResult result = mockMvc.perform(post("/accounts/addAccount").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isInternalServerError()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Failed to add Account"));

    }

    @Test
    public void updateAccountTest() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.updateAccount(ArgumentMatchers.any(),ArgumentMatchers.anyInt())).thenReturn(account);
        MvcResult result = mockMvc.perform(put("/accounts/updateAccount/{accountId}",1).content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Updated the account successfully for ID"));

    }

    @Test
    public void updateAccountTestException() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.updateAccount(ArgumentMatchers.any(),ArgumentMatchers.anyInt())).thenThrow(new RuntimeException("Exception occured"));
        MvcResult result = mockMvc.perform(put("/accounts/updateAccount/{accountId}",1).content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isInternalServerError()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Failed to update Account"));

    }
    @Test
    public void updateAccountTestNotFoundException() throws Exception {
        Account account = new Account();
        account.setBalance(1234);
        account.setName("testAccount");
        String jsonRequest = om.writeValueAsString(account);
        Mockito.when(accountService.updateAccount(ArgumentMatchers.any(),ArgumentMatchers.anyInt())).thenThrow(new NotFoundException("Exception occured"));
        MvcResult result = mockMvc.perform(put("/accounts/updateAccount/{accountId}",1).content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Failed to update Account"));

    }
    @Test
    public void findByNumberOrNameTest() throws Exception {
        List<AccountDTO> accountDTOList=new ArrayList<>();
        AccountDTO accountDTO=new AccountDTO();
        accountDTO.setName("test1");
        accountDTOList.add(accountDTO);
        AccountDTO accountDTO1=new AccountDTO();
        accountDTO1.setName("test2");
        accountDTOList.add(accountDTO1);
        Mockito.when(accountService.getAccountsByNumberOrName(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(accountDTOList);
        MvcResult result = mockMvc.perform(get("/accounts/findByNumberOrName")
                .param("accountNumber","1")
                                .param("accountName","test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);

    }
    @Test
    public void findByNumberOrNameTestException() throws Exception {
        Mockito.when(accountService.getAccountsByNumberOrName(ArgumentMatchers.any(),ArgumentMatchers.any())).thenThrow(new RuntimeException("Exception Occured"));
        MvcResult result = mockMvc.perform(get("/accounts/findByNumberOrName")
                .param("accountNumber","1")
                .param("accountName","test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isInternalServerError()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);

    }

    @Test
    public void getAllAccountsWithPaginationTest() throws Exception {
        List<AccountDTO> accountDTOList=new ArrayList<>();
        AccountDTO accountDTO=new AccountDTO();
        accountDTO.setName("test1");
        accountDTOList.add(accountDTO);
        AccountDTO accountDTO1=new AccountDTO();
        accountDTO1.setName("test2");
        accountDTOList.add(accountDTO1);
        Mockito.when(accountService.getAccountWithPagination(ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(accountDTOList);
        MvcResult result = mockMvc.perform(get("/accounts/getAllAccountWithPagination/")
                        .param("offSet","1")
                        .param("pageSize","1")
                        .param("sortBy","test")
                        .param("sortDirection","test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);

    }
    @Test
    public void loginUserTestSuccess() throws Exception {
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        String jsonRequest = om.writeValueAsString(authorization);
        Mockito.when(accountSecurity.AuthenticateUser(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(accountService.checkUser(authorization)).thenReturn(Boolean.TRUE);
        Mockito.when(jwtUtils.generateToken(ArgumentMatchers.anyString())).thenReturn("testString");
        MvcResult result = mockMvc.perform(post("/accounts/login").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
    }

    @Test
    public void loginUserTestAlreadyLoggedIn() throws Exception {
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        String jsonRequest = om.writeValueAsString(authorization);
        Mockito.when(accountSecurity.AuthenticateUser(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(accountService.checkUser(authorization)).thenReturn(Boolean.FALSE);
        Mockito.when(jwtUtils.generateToken(ArgumentMatchers.anyString())).thenReturn("testString");
        MvcResult result = mockMvc.perform(post("/accounts/login").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("User already authenticated.No need to log in again."));
    }
    @Test
    public void loginUserTestFail() throws Exception {
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        String jsonRequest = om.writeValueAsString(authorization);
        Mockito.when(accountSecurity.AuthenticateUser(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(jwtUtils.generateToken(ArgumentMatchers.anyString())).thenReturn("testString");
        MvcResult result = mockMvc.perform(post("/accounts/login").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
    }

    @Test
    public void loginOutTest() throws Exception {
        Mockito.when(accountService.doLogout("admin")).thenReturn(true);
        MvcResult result = mockMvc.perform(post("/accounts/logout/{userName}","admin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Logout Successfull"));
    }
    @Test
    public void loginOutTestFail() throws Exception {
        Mockito.when(accountService.doLogout("admin")).thenReturn(false);
        MvcResult result = mockMvc.perform(post("/accounts/logout/{userName}","admin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
        Assert.assertTrue(resultContent.contains("Logout failed"));
    }

    @Test
    public void getAccountsTest() throws Exception {
        GetAccountsResponseBody getAccountsResponseBody=new GetAccountsResponseBody();
        getAccountsResponseBody.setTotal(2L);
        Mockito.when(accountService.getAccounts()).thenReturn(getAccountsResponseBody);
        MvcResult result = mockMvc.perform(get("/accounts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assert.assertNotNull(resultContent);
    }

}