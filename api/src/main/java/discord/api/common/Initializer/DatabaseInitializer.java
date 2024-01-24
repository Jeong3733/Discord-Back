package discord.api.common.Initializer;

import discord.api.entity.Server;
import discord.api.entity.User;
import discord.api.entity.connectionEntity.UserServer;
import discord.api.entity.enums.Role;
import discord.api.entity.enums.UserStatus;
import discord.api.repository.User.UserRepository;
import discord.api.repository.UserServer.UserServerRepository;
import discord.api.repository.server.ServerRepository;
import groovy.util.logging.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("dev")
@Component
@AllArgsConstructor
public class DatabaseInitializer {
    private UserRepository userRepository;
    private ServerRepository serverRepository;
    private UserServerRepository userServerRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        // BCryptPasswordEncoder 인스턴스 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 사용자 데이터 생성 및 저장
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .email("user" + i + "@example.com")
                    .nickname("nickname" + i)
                    .password(encoder.encode("Password#" + i))
                    .userStatus(UserStatus.OFFLINE)
                    .birth(LocalDate.now())
                    .role(Role.USER)
                    .build();

            users.add(user);
        }
        userRepository.saveAll(users);

        // 서버 데이터 생성 및 저장
        List<Server> servers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Server server = Server.builder()
                    .name("Team" + i)
                    .description("Team " + i + " Description")
                    .build();

            servers.add(server);
        }
        serverRepository.saveAll(servers);

        // user_server 데이터 생성 및 저장
        List<UserServer> userServerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserServer userServer = UserServer.builder()
                    .user(users.get(i))
                    .server(servers.get(0))
                    .userStatus(UserStatus.OFFLINE)
                    .build();
            userServerList.add(userServer);
        }

        for (int i = 3; i < 6; i++) {
            UserServer userServer = UserServer.builder()
                    .user(users.get(i))
                    .server(servers.get(0))
                    .userStatus(UserStatus.OFFLINE)
                    .build();
            userServerList.add(userServer);
        }

        for (int i = 6; i < 9; i++) {
            UserServer userServer = UserServer.builder()
                    .user(users.get(i))
                    .server(servers.get(0))
                    .userStatus(UserStatus.OFFLINE)
                    .build();
            userServerList.add(userServer);
        }

        userServerRepository.saveAll(userServerList);
    }
}
