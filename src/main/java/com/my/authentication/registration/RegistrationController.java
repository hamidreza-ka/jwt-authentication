package com.my.authentication.registration;

import com.my.authentication.appuser.AppUser;
import com.my.authentication.jwt.JwtResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @PostMapping("/renew")
    public String renewToken(@RequestParam("email") String email) {
        return registrationService.renewToken(email);
    }

    @GetMapping("/confirm")
    public JwtResponse confirm(@RequestParam("token") String token, @RequestParam("grant_type") String grantType) {

        if (grantType.equals("password"))
            return registrationService.confirmToken(token);

        throw new IllegalArgumentException("grant_type is invalid");
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestParam("username") String userName, @RequestParam("password") String password)
    {
        return registrationService.login(userName, password);
    }

    @GetMapping("/refresh")
    private JwtResponse refresh(@RequestParam("refresh_token") String refreshToken, @RequestParam("grant_type") String grantType) {
        if (grantType.equals("refresh"))
            return registrationService.confirmRefreshToken(refreshToken);

        throw new IllegalArgumentException("grant_type is invalid");
    }
}
