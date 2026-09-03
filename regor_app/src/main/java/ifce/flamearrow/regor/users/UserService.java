package ifce.flamearrow.regor.users;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import ifce.flamearrow.regor.remote.JwtService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

@Service
class UserService {
	
	private final JdbcTemplate jdbc;
	private final JwtService jwtServicer;
	private final BCryptPasswordEncoder passwordEncoder;
	
	UserService (JdbcTemplate jdbc, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
		this.jdbc = jdbc;
		this.jwtServicer = jwtService;
		this.passwordEncoder = passwordEncoder;
	}
	
	UUID getUUIDByUsername (String username) {
		String sql = "SELECT id FROM users WHERE username = ?";
		
		try {
			return jdbc.queryForObject(sql, UUID.class, username);
		} catch (EmptyResultDataAccessException err) {
			return null;
		}
	}
	
	String getHashedPasswordFromUUID (UUID targetUUID) {
		String sql = "SELECT password_hash FROM users WHERE id = ?";
		
		 try {
			 return jdbc.queryForObject(sql, String.class, targetUUID);
		 } catch (EmptyResultDataAccessException err) {
			 return null;
		 }
	}
	
	List<UUID> getFollowersFromUUID (UUID followedUUID) {
		String sql = "SELECT following_id FROM follows WHERE followed_id = ?";
		
		return jdbc.queryForList(sql, UUID.class, followedUUID);		
	}
	
	String register (String email, String passwordPlaintext, String username) throws IllegalArgumentException, Exception {

		String passwordHashed = passwordEncoder.encode(passwordPlaintext);
		UUID generatedUUID;
		
		if (username.length() > 32) {
			throw new IllegalArgumentException("Username too long");
		} else if (jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username) != 0) {
			throw new Exception("Username already exists");
		} else if (jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email) != 0) {
			throw new Exception("Email already registered");
		} else {
			String sql = "INSERT INTO users (email, username, password_hash) VALUES (?, ?, ?) RETURNING id";
			
			generatedUUID = jdbc.queryForObject(sql, UUID.class, email, username, passwordHashed);
		}
		
		return jwtServicer.generateToken(generatedUUID);
	}
	
	String login (String username, String passwordPlaintext) throws SecurityException, Exception {
		UUID targetUUID = getUUIDByUsername(username);
		if (targetUUID == null) {
			throw new Exception("Requested user does not exist");
		}
		
		if (!passwordEncoder.matches(passwordPlaintext, getHashedPasswordFromUUID(targetUUID))) {
			throw new SecurityException("Invalid credentials");
		}
		
		return jwtServicer.generateToken(targetUUID);
	}

}
