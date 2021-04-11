package com.my.authentication.registration.blacklist;

import com.my.authentication.appuser.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    @Autowired
    public BlackListTokenService(BlackListTokenRepository blackListTokenRepository) {
        this.blackListTokenRepository = blackListTokenRepository;
    }

    public void addToBlackList(String tokenId, AppUser user){
        blackListTokenRepository.save(new BlackListToken(tokenId, user));
    }

    public boolean searchInBlackList(String tokenId, AppUser user){
        return blackListTokenRepository.existsBlackListTokenByTokenIdAndUser(tokenId, user);
    }
}
