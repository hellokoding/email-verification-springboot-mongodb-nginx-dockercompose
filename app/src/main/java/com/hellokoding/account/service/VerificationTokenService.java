package com.hellokoding.account.service;

import com.hellokoding.account.model.User;
import com.hellokoding.account.model.VerificationToken;
import com.hellokoding.account.repository.UserRepository;
import com.hellokoding.account.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationTokenService {
    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private SendingMailService sendingMailService;

    @Autowired
    public VerificationTokenService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, SendingMailService sendingMailService){
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.sendingMailService = sendingMailService;
    }

    public void createVerification(String email){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            userRepository.save(user);
        }

        VerificationToken verificationToken = verificationTokenRepository.findByUserEmail(email);
        if (verificationToken == null) {
            verificationToken = new VerificationToken();
            verificationToken.setUser(user);
            verificationTokenRepository.save(verificationToken);
        }

        sendingMailService.sendVerificationMail(email, verificationToken.getToken());
    }

    public ResponseEntity<String> verifyEmail(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            ResponseEntity.badRequest().body("Invalid token.");
        }

        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            ResponseEntity.unprocessableEntity().body("Expired token.");
        }

        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        verificationToken.getUser().setIsActive(true);
        verificationTokenRepository.save(verificationToken);

        return ResponseEntity.ok("You have successfully verified your email address.");
    }
}
