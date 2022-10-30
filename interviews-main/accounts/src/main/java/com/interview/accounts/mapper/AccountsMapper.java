package com.interview.accounts.mapper;

import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.domain.entity.JwtDetails;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import org.springframework.util.ObjectUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public final class AccountsMapper {

    public static List<AccountDTO> map(List<Account> accounts) {
        return accounts.stream()
                .map(account -> new AccountDTO(account.getNumber(), account.getName(), account.getBalance()))
                .collect(Collectors.toList());
    }

    public static JwtDetails mapAuthtoJwt(Authorization authorization){
        JwtDetails jwtDetails=new JwtDetails();
        jwtDetails.setUsername(authorization.getUserName());
        jwtDetails.setPassword(authorization.getPassword());
        jwtDetails.setLogout(false);
        Calendar currentTimeNow = Calendar.getInstance();
        currentTimeNow.add(Calendar.MINUTE, 2);
        Date expiryDate = currentTimeNow.getTime();
        jwtDetails.setExpiration(expiryDate);
        return jwtDetails;
    }

    public static Account mapDtoToEntity(AccountDTO accountDto,int id) {
            Account account=new Account();
            account.setName(accountDto.getName());
            account.setNumber(accountDto.getNumber());
            account.setBalance(accountDto.getBalance());
            if(!ObjectUtils.isEmpty(id)){
                account.setId(id);
            }
            return account;
    }
}
