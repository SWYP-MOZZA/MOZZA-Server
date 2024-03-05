package shop.mozza.app.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.mozza.app.user.domain.User;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Boolean isMember;

    // User 엔티티를 UserDto로 변환하는 정적 메소드
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
//                .createdAt(user.getCreatedAt())
                .isMember(user.getIsMember())
                .build();
    }

}
