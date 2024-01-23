package com.hansori.ws.db.mysql.user;


import com.hansori.ws.db.mysql.base.TimeAudit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends TimeAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private boolean emailAuthenticated;
    private String password;
    private UUID profile_image;
    private String profile_message;
    private String nickname;
    private LocalDateTime birth;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private Role role;
}
