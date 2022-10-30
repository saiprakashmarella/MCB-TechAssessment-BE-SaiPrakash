package com.interview.accounts.mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.domain.entity.JwtDetails;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountsMapperTest {


    @Test
    public void mapTest(){
        List<Account> accounts=new ArrayList<>();
        accounts.add(getAccountWithName("test1"));
        accounts.add(getAccountWithName("test2"));
        accounts.add(getAccountWithName("test3"));
        List<AccountDTO> accountDTOS=AccountsMapper.map(accounts);
        Assert.assertNotNull(accountDTOS);
        Assert.assertEquals(accounts.size(),accountDTOS.size());
    }

    @Test
    public void mapAuthtoJwtTest(){
        Authorization authorization=new Authorization();
        authorization.setUserName("admin");
        authorization.setPassword("password");
        JwtDetails jwtDetails=AccountsMapper.mapAuthtoJwt(authorization);
        Assert.assertNotNull(jwtDetails);
        Assert.assertNotNull(jwtDetails.getLogout());
        Assert.assertNotNull(jwtDetails.getExpiration());
        Assert.assertNotNull(jwtDetails.getUsername());
        Assert.assertNotNull(jwtDetails.getPassword());
    }

    @Test
    public void mapDtoToEntityTest(){
        AccountDTO accountDTO=getAccountDtoWithName("test1");
        accountDTO.setNumber(1234);
        accountDTO.setBalance(1234);
        Account account=AccountsMapper.mapDtoToEntity(accountDTO,1);
        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getName());
        Assert.assertEquals(accountDTO.getNumber(),account.getNumber());
        Assert.assertTrue(accountDTO.getNumber()==account.getNumber());
        Assert.assertEquals(accountDTO.getName(),account.getName());


    }

    private Account getAccountWithName(String name) {
        Account account=new Account();
        account.setName(name);
        return account;
    }
    private AccountDTO getAccountDtoWithName(String name) {
        AccountDTO account=new AccountDTO();
        account.setName(name);
        return account;
    }
}
