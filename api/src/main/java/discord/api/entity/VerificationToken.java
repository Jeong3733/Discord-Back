package discord.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationToken {
    private static final int EXPIRATION = 60; // 60m
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime expiryDate;

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(VerificationToken.EXPIRATION);
    }

    @Builder
    public VerificationToken(Long id, String token, User user) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }
}
