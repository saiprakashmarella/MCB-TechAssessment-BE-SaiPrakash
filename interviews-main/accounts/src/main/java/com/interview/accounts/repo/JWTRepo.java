package com.interview.accounts.repo;

import com.interview.accounts.domain.entity.JwtDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JWTRepo extends JpaRepository<JwtDetails, Long> {

    JwtDetails findByUsernameAndPassword(String username, String password);
    JwtDetails findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "update JwtDetails jwt set jwt.logout = ? where jwt.username = ?",
            nativeQuery = true)
    void updateLogoutByUser(boolean logout,String userName);
}
