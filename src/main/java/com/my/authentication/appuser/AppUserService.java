package com.my.authentication.appuser;

import com.my.authentication.registration.token.ConfirmationToken;
import com.my.authentication.registration.token.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public Optional<AppUser> findByEmail(String email){
        return appUserRepository.findByEmail(email);
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getUsername()).isPresent();
        if (userExists) {
//             check of attribute are the same and
//             if email not confirmed send confirmation email
            if (!appUser.isEnabled()) {
                return createToken(appUser, true);
            } else
                throw new IllegalStateException("email already taken");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        //  send email
        return createToken(appUser, false);
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableUser(email);
    }

    private String createToken(AppUser appUser, boolean isRenew) {
        ConfirmationToken confirmationToken;
        String token = UUID.randomUUID().toString();


        if (isRenew) {
            confirmationToken = confirmationTokenService.getTokenByUserId(appUser.getId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            confirmationToken.setToken(token);
            confirmationToken.setCreatedAt(LocalDateTime.now());
            confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        } else {

            confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    appUser
            );

        }

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

}
