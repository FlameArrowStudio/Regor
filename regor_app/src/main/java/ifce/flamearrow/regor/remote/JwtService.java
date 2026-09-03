package ifce.flamearrow.regor.remote;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey key = Keys.hmacShaKeyFor(
		System.getenv("JWT_SECRET").getBytes()
	);

	public String generateToken (UUID userId) {
		return Jwts.builder()
		    .subject(userId.toString())
		    .issuedAt(new Date())
		    //uma sessao expira em 60 dias 
		    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 60))
		    .signWith(key)
		    .compact();
	}

	public UUID validateAndGetUserId (String token) {
		String subject = Jwts.parser()
		    .verifyWith(key)
		    .build()
		    .parseSignedClaims(token)
		    .getPayload()
		    .getSubject();
		       
		return UUID.fromString(subject);
	}
}
