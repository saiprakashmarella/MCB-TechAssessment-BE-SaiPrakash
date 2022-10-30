package com.interview.accounts.service;

import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.domain.entity.JwtDetails;
import com.interview.accounts.exception.AlreadyExistException;
import com.interview.accounts.exception.NotFoundException;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.repo.AccountRepository;
import com.interview.accounts.repo.JWTRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JWTRepo jwtRepo;

    @Test
    public void getAccountsTest(){
        Mockito.when(accountRepository.count()).thenReturn(2L);
        List<Account> accountList=new ArrayList<>();
        Account account=getAccountWithName("test1");
        accountList.add(account);
        Account account2=getAccountWithName("test2");
        accountList.add(account2);
        Mockito.when(accountRepository.findAll()).thenReturn(accountList);
        GetAccountsResponseBody getAccountsResponseBody=accountService.getAccounts();
        Assert.assertNotNull(getAccountsResponseBody);
        Assert.assertEquals(2L,getAccountsResponseBody.getTotal());

    }

    @Test
    public void addAccountTest() throws AlreadyExistException {
        AccountDTO accountDTO=getAccountDtoWithName("test1");
        Account account=getAccountWithName("test1");
        Mockito.when(accountRepository.save(ArgumentMatchers.any())).thenReturn(account);
        Account respone= accountService.addAccount(accountDTO);
        Assert.assertNotNull(respone);
        Assert.assertEquals(account,respone);
    }

    @Test
    public void addAccountTestException(){
        Account account=getAccountWithName("test1");
        AccountDTO accountDTO=getAccountDtoWithName("test1");
        accountDTO.setBalance(1234);
        accountDTO.setNumber(1234);
        Mockito.when(accountRepository.findFirstByNumberAndNameAndBalance(1234,"test1",1234)).thenReturn(account);
        AlreadyExistException exception=Assert.assertThrows(AlreadyExistException.class,() -> {
            accountService.addAccount(accountDTO);
        });
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception.getMessage().contains("Account already exist. Try to update it."));
    }

    @Test
    public void updateAccountTest() throws NotFoundException {
        Account account=getAccountWithName("test1");
        Mockito.when(accountRepository.save(ArgumentMatchers.any())).thenReturn(account);
        Mockito.when(accountRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.of(account));
        AccountDTO accountDTO=getAccountDtoWithName("test1");
        Account response= accountService.updateAccount(accountDTO,1);
        Assert.assertNotNull(response);
        Assert.assertEquals(account,response);
    }
    @Test
    public void updateAccountTestThrowNotFoundException(){
        AccountDTO account=getAccountDtoWithName("test1");
        Mockito.when(accountRepository.findById(ArgumentMatchers.anyInt())).thenReturn(null);
       NotFoundException exception=Assert.assertThrows(NotFoundException.class,() -> {
           accountService.updateAccount(account, 1);
       });
       Assert.assertNotNull(exception);
       Assert.assertTrue(exception.getMessage().contains("Account not found for ID"));
    }

    @Test
    public void getAccountsByNumberOrNameTest(){
        List<Account> accountList=new ArrayList<>();
        accountList.add(getAccountWithName("test1"));
        accountList.add(getAccountWithName("test2"));
        Mockito.when(accountRepository.findByNumberOrName(ArgumentMatchers.anyInt(),ArgumentMatchers.anyString())).thenReturn(accountList);
        List<AccountDTO> response=accountService.getAccountsByNumberOrName(Optional.of(1), Optional.of("test1"));
        Assert.assertNotNull(response);
    }

    @Test
    public void checkUserValidityTestTrue(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(false);
        Mockito.when(jwtRepo.findByUsernameAndPassword(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(jwtDetails);
        Boolean response=accountService.checkUserValidity("admin","password");
        Assert.assertTrue(response);
    }

    @Test
    public void checkUserValidityTestFalse(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(true);
        Mockito.when(jwtRepo.findByUsernameAndPassword(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(jwtDetails);
        Boolean response=accountService.checkUserValidity("admin","password");
        Assert.assertFalse(response);
    }

    @Test
    public void checkUserValidityTestNoData(){

        Mockito.when(jwtRepo.findByUsernameAndPassword(ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(null);
        Boolean response=accountService.checkUserValidity("admin","password");
        Assert.assertTrue(response);
    }

    @Test
    public void doLogoutTest(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(true);
        Mockito.when(jwtRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(jwtDetails);
        Mockito.doNothing().when(jwtRepo).updateLogoutByUser(ArgumentMatchers.anyBoolean(),ArgumentMatchers.anyString());
        Boolean response=accountService.doLogout("admin");
        Assert.assertTrue(response);
    }

    @Test
    public void doLogoutTestNoData(){
        Mockito.when(jwtRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        Boolean response=accountService.doLogout("admin");
        Assert.assertFalse(response);
    }

    @Test
    public void checkUserTestTrue(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(false);
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        Assert.assertTrue(accountService.checkUser(authorization));
    }

    @Test
    public void checkUserTestNoData(){
        JwtDetails jwtDetails= new JwtDetails();
        jwtDetails.setUsername("admin");
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        Mockito.when(jwtRepo.save(ArgumentMatchers.any())).thenReturn(jwtDetails);
        Assert.assertTrue(accountService.checkUser(authorization));
    }

    @Test
    public void checkUserTestFalse(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(Boolean.FALSE);
        jwtDetails.setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        Mockito.when(jwtRepo.findByUsernameAndPassword("admin","password")).thenReturn(jwtDetails);
        Assert.assertFalse(accountService.checkUser(authorization));
    }
    @Test
    public void checkUserTestTrueData(){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername("admin");
        jwtDetails.setLogout(Boolean.TRUE);
        jwtDetails.setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        Mockito.when(jwtRepo.findByUsernameAndPassword("admin","password")).thenReturn(jwtDetails);
        Assert.assertTrue(accountService.checkUser(authorization));
    }

    private AccountDTO getAccountDtoWithName(String name) {
        AccountDTO account=new AccountDTO();
        account.setName(name);
        return account;
    }

    private Account getAccountWithName(String name) {
        Account account=new Account();
        account.setName(name);
        return account;
    }

}
