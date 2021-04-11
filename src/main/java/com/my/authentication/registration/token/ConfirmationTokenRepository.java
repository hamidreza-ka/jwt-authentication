package com.my.authentication.registration.token;

import com.my.authentication.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByAppUserId(Long userId);

    void deleteByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken ct SET ct = ?2 WHERE ct.id = ?1")
    int updateToken(Long tokenId, ConfirmationToken confirmationToken);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken ct SET ct.confirmedAt = ?2 WHERE ct.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
