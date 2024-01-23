package com.hansori.ws.db.mysql.server;

import com.hansori.ws.db.mysql.base.TimeAudit;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Server extends TimeAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Column(nullable = true)
    private UUID profileImage;

}
