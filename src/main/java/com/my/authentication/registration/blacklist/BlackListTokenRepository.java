package com.my.authentication.registration.blacklist;

import com.my.authentication.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {

    Optional<BlackListToken> findByTokenIdAndUser(String tokenId, AppUser user);
    boolean existsBlackListTokenByTokenIdAndUser(String tokenId, AppUser user);
}
