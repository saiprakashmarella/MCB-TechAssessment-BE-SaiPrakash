package com.interview.accounts.controller;

import com.interview.accounts.domain.entity.Account;
import com.interview.accounts.exception.AlreadyExistException;
import com.interview.accounts.exception.NotFoundException;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.Authorization;
import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.security.AccountSecurity;
import com.interview.accounts.service.AccountService;
import com.interview.accounts.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountsController {


    private final AccountService accountService;

    private final AccountSecurity accountSecurity;
    private final JWTUtils jwtUtils;



    @PostMapping("/login")
    public String loginUser(@RequestBody Authorization authorization){
        log.info("Inside AccountsController.loginUser");
        log.info("Doing login  for user:{}",authorization.getUserName());
        if(Boolean.TRUE.equals(accountSecurity.AuthenticateUser(authorization.getUserName(), authorization.getPassword()))){
            if(accountService.checkUser(authorization)){
                log.info("User authenticated successfully. Generating the token");
                return jwtUtils.generateToken(authorization.getUserName());
            }
            else{
                log.info("User already authenticated.No need to log in again");
                return "User already authenticated.No need to log in again.";
            }


        }else{
            log.error("Invalid Username/Password");
            return "Invalid Username/Password";
        }
    }
    @PostMapping("/logout/{userName}")
    public String logoutbyUser(@PathVariable String userName){
        log.info("Inside AccountsController.logoutSuccessful");
        if(accountService.doLogout(userName)){
              return "Logout Successfull";
          }
        else{
            return "Logout failed";
        }

    }


    @GetMapping
    public ResponseEntity<GetAccountsResponseBody> getAccounts() {
        log.info("Inside AccountsController.getAccounts");
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/getAllAccountWithPagination")
    public List<AccountDTO> getAllAccountsWithPagination(@RequestParam("offSet") int offSet,@RequestParam("pageSize") int pageSize,@RequestParam(value = "sortBy",defaultValue = "name",required = false) String sortBy,@RequestParam(value = "sortDirection",defaultValue = "asc",required = false) String sortDirection){
        log.info("Inside AccountsController.getAllAccountsWithPagination");
        log.info("performing getAllAccountsWithPagination with offSet:{} , pageSize: {}",offSet,pageSize);
        return accountService.getAccountWithPagination(offSet, pageSize,sortBy,sortDirection);
    }

    @GetMapping("/findByNumberOrName")
    public ResponseEntity<List<AccountDTO>> findByNumberOrName(@RequestParam("accountNumber") Optional<Integer> accountNumber,
                                                            @RequestParam("accountName") Optional<String> accountName){
        log.info("Inside AccountsController.findByNumberOrName");
        log.info("Fetching Accounts by Number or Name");
        List<AccountDTO> accounts=new ArrayList<>();
        try{
             accounts=accountService.getAccountsByNumberOrName(accountNumber,accountName);
            return new ResponseEntity<>(accounts,HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception Occurred: {}",e.getMessage());
            return new ResponseEntity<>(accounts,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addAccount")
    public ResponseEntity<String> saveAccount(@RequestBody AccountDTO accountDTO){
        log.info("Inside AccountsController.findByNumberOrName");
        log.info("performing Save Account");
        try{
            Account accountDetails=accountService.addAccount(accountDTO);
            log.info("Save Account successfully with ID:{}",accountDetails.getId());
            return new ResponseEntity<>("added the account successfully with Id:"+accountDetails.getId(), HttpStatus.OK);
        }catch (Exception e){
            if( e instanceof AlreadyExistException){
                log.info("Account details already exist. Try updating them instead");
                return new ResponseEntity<>("Failed to add Account :"+e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            log.error("Exception Occurred : {}",e.getMessage());
            return new ResponseEntity<>("Failed to add Account :"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/updateAccount/{accountId}")
    public ResponseEntity<String> updateAccount(@PathVariable int accountId, @RequestBody AccountDTO accountDto){
        log.info("Inside AccountsController.updateAccount");
        log.info("performing update Account");
        try{
            Account accountUpdated=accountService.updateAccount(accountDto,accountId);
            log.info("Update Account successfully");
            return new ResponseEntity<>("Updated the account successfully for ID:"+accountUpdated.getId(), HttpStatus.OK);
        }catch (Exception e){
            if(e instanceof NotFoundException){
                log.error("NotFoundException Occurred : {}",e.getMessage());
                return new ResponseEntity<>("Failed to update Account :"+e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            else{
                log.error("Exception Occurred : {}",e.getMessage());
                return new ResponseEntity<>("Failed to update Account :"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
    }
}
