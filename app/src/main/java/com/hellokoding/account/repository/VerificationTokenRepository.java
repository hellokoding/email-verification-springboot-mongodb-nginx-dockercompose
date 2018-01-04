package com.hellokoding.account.repository;

import com.hellokoding.account.model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    VerificationToken findByUserEmail(String email);
    VerificationToken findByToken(String token);
}
