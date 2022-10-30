package com.interview.accounts.service;

import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.exception.AlreadyExistException;
import com.interview.accounts.exception.NotFoundException;
import com.interview.accounts.mapper.AccountsMapper;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.domain.entity.JwtDetails;
import com.interview.accounts.repo.AccountRepository;
import com.interview.accounts.repo.JWTRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {

    private final AccountRepository repository;
    private final JWTRepo jwtRepo;


    public GetAccountsResponseBody getAccounts() {

        return new GetAccountsResponseBody(repository.count(),AccountsMapper.map(repository.findAll()));
    }

    public Account addAccount(AccountDTO account) throws AlreadyExistException {
        log.info("Inside AccountService.addaccount");
        if(ObjectUtils.isEmpty(repository.findFirstByNumberAndNameAndBalance(account.getNumber(),account.getName(), account.getBalance()))) {
            return repository.save(AccountsMapper.mapDtoToEntity(account,0));
        }
        else{
            throw new AlreadyExistException("Account already exist. Try to update it.");
        }
    }

    public Account updateAccount(AccountDTO account, int accountId) throws NotFoundException {
        log.info("Inside AccountService.updateAccount");
        log.info("Fetching the account details for id:{}",accountId);
        Optional<Account> findAccount=repository.findById(accountId);
        if(!ObjectUtils.isEmpty(findAccount)){
            log.info("Account details found for id:{}. Updating the details for the id:{}",accountId,accountId);
           return repository.save(AccountsMapper.mapDtoToEntity(account,accountId));
        }
        else{
            log.error("Account details not found for id:{}",accountId);
            throw new NotFoundException("Account not found for ID:"+accountId);
        }
    }

    public List<AccountDTO> getAccountsByNumberOrName(Optional<Integer> accountNumber, Optional<String> accountName) {
        log.info("Inside AccountService.getAccountsByNumberOrName");
        String accountNam=accountName.orElse("");
        Integer accountNum=accountNumber.orElse(null);
        return AccountsMapper.map(repository.findByNumberOrName(accountNum,accountNam));
    }

    public List<AccountDTO> getAccountWithPagination(int offset,int pageNumber, String sortBy, String sortDir){
        log.info("Inside AccountService.getAccountWithPagination");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Page<Account> accounts=repository.findAll(PageRequest.of(offset,pageNumber,sort));
        List<Account> accountList=accounts.getContent();
        return AccountsMapper.map(accountList);
    }

    public boolean checkUser(Authorization authorization) {
        log.info("Inside AccountService.generateToken ");
        JwtDetails jwtDetails=jwtRepo.findByUsernameAndPassword(authorization.getUserName(), authorization.getPassword());

        if(ObjectUtils.isEmpty(jwtDetails)){
            JwtDetails jwtDetailsEntity=AccountsMapper.mapAuthtoJwt(authorization);
            jwtRepo.save(jwtDetailsEntity);
            log.info("JWTdetails saved with id:{}",jwtDetailsEntity.getId());
            return true;
        }
        else{

            if(Boolean.FALSE.equals(jwtDetails.getLogout()) && jwtDetails.getExpiration().compareTo(Calendar.getInstance().getTime())>0){
                log.info("User : {} already authenticated. No need to do it again",authorization.getUserName());
                jwtRepo.updateLogoutByUser(false,authorization.getUserName());
                return false;
            }
            else{
                jwtRepo.updateLogoutByUser(false,authorization.getUserName());
                return true;
            }
        }

    }

    public boolean doLogout(String username) {
        log.info("Inside AccountService.doLogout ");
        if(!ObjectUtils.isEmpty(jwtRepo.findByUsername(username))){
            log.info("Found existing login details. Updating them to logout");
            jwtRepo.updateLogoutByUser(true,username);
        return true;
        }
        else{
            log.info("Not Found existing login details.");
            return false;
        }
    }

    public boolean checkUserValidity(String username, String password) {
        log.info("Inside AccountService.checkUserValidity");
        JwtDetails jwtDetails=jwtRepo.findByUsernameAndPassword(username,password);

        if(ObjectUtils.isEmpty(jwtDetails)){
            log.info("Not Found any existing userDetails. Hence proceeding with login");
            return  true;
        }
        else{
            log.info(" Found existing userDetails");
            return !jwtDetails.getLogout();
        }
    }
}
