package shop.mozza.app.user.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.mozza.app.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime refreshTokenExpires;

    public UserOAuth(User user, String providerUserId) {
        this.user = user;
    }
}