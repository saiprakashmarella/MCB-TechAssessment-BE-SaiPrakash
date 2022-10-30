package com.interview.accounts.repo;

import com.interview.accounts.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    List<Account> findByNumberOrName(Integer number, String name);
    Account findFirstByNumberAndNameAndBalance(int number,String name, double Balance);

}
