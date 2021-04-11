package com.my.authentication.registration.blacklist;

import com.my.authentication.appuser.AppUser;

import javax.persistence.*;

@Entity
public class BlackListToken {

    @SequenceGenerator(
            name = "black_list_token_sequence",
            sequenceName = "black_list_token_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "black_list_token_sequence"
    )
    private Long id;

    private String tokenId;

    @ManyToOne
    @JoinColumn(nullable = false,
    name = "app_user_id")
    private AppUser user;

    public BlackListToken(String tokenId, AppUser user) {
        this.tokenId = tokenId;
        this.user = user;
    }

    public BlackListToken() {
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
