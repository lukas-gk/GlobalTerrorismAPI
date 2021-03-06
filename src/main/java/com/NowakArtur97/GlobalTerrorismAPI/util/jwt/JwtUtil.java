package com.NowakArtur97.GlobalTerrorismAPI.util.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtUtil {

    String generateToken(UserDetails userDetails);

    Boolean isTokenValid(String token, UserDetails userDetail);

    String extractUserName(String token);

    Date extractExpirationDate(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
