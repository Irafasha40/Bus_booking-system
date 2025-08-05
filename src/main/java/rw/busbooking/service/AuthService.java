package rw.busbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rw.busbooking.dtos.AuthResponseDTO;
import rw.busbooking.dtos.LoginRequestDTO;
import rw.busbooking.dtos.UserRequestDTO;
import rw.busbooking.dtos.UserResponseDTO;
import rw.busbooking.model.PasswordResetToken;
import rw.busbooking.model.Role;
import rw.busbooking.model.User;
import rw.busbooking.repository.PasswordResetTokenRepository;
import rw.busbooking.repository.UserRepository;
import rw.busbooking.security.JwtService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserResponseDTO register(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);

        return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token, new UserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name()));
    }
    public void initiateReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email not found");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        PasswordResetToken token = new PasswordResetToken(
                null, otp, email,
                LocalDateTime.now().plusMinutes(10),
                false
        );

        tokenRepository.save(token);

        emailService.sendOtpEmail(email, otp); // Implement email sending
    }

    public void resetPassword(String email, String otp, String newPassword) {
        PasswordResetToken token = tokenRepository.findByTokenAndEmail(otp, email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (token.isUsed() || token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Expired or already used OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }


}

