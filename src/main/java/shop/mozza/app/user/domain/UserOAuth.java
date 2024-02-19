package shop.mozza.app.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
//    @JoinColumn(name = "oauth_user_id")
    private User user;

    @Column
    private String providerUserId;

    @Column
    private String accessToken;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime accessTokenExpires;

    @Column
    private LocalDateTime refreshTokenExpires;



}