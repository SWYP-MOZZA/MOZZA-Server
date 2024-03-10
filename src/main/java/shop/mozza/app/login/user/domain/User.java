package shop.mozza.app.login.user.domain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mozza.app.util.BaseTimeEntity;

import java.time.LocalDateTime;


@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Boolean isMember;

    @Column
    private String role;

    @Column
    private String password;

    @Column
    private String email;


    @Builder
    public User(String name, Boolean isMember, String role, String password, String email) {
        this.name = name;
        this.isMember = isMember;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    @Builder
    public User(String name, Boolean isMember, String role) {
        this.name = name;
        this.isMember = isMember;
        this.role = role;
    }


    public void updateUserName(String name){
        this.name = name;
    }
    public void updateUserEmail(String email){
        this.email = email;
    }


}
