package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query("""
        SELECT t FROM Token t INNER JOIN User u 
        ON t.user.id = u.id
        WHERE t.user.id= :userId AND t.loggedOut = false
        """)
    List<Token> findAllAccessTokenByUser(Long userId);

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

}
