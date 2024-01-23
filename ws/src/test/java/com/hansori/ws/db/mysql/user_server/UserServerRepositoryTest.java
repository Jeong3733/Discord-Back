package com.hansori.ws.db.mysql.user_server;

import com.hansori.ws.db.mysql.server.Server;
import com.hansori.ws.db.mysql.server.ServerRepository;
import com.hansori.ws.db.mysql.user.User;
import com.hansori.ws.db.mysql.user.UserRepository;
import com.hansori.ws.db.mysql.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServerRepositoryTest {

    @Autowired
    private UserServerRepository userServerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServerRepository serverRepository;

    @Test
    void updateUserServerToOnline() {

        final Server server = Server.builder()
                .id(1L)
                .build();
        final User user = User.builder()
                .id(1L)
                .build();

        final Long serverId = serverRepository.save(server).getId();
        final Long userId = userRepository.save(user).getId();


        final UserServer userServer = UserServer.builder()
                .server(server)
                .user(user)
                .userStatus(UserStatus.OFFLINE)
                .build();


        final UserServer saved = userServerRepository.save(userServer);
        userServerRepository.updateUserServer(serverId, userId, UserStatus.ONLINE);
        userServerRepository.flush();

        final UserServer updated = userServerRepository.findById(saved.getId()).get();


        assertEquals(UserStatus.ONLINE, updated.getUserStatus());
    }

    @Test
    void updateUserServerToOffline() {

        final Server server = Server.builder()
                .build();
        final User user = User.builder()
                .build();

        final Long serverId = serverRepository.save(server).getId();
        final Long userId = userRepository.save(user).getId();


        final UserServer userServer = UserServer.builder()
                .server(server)
                .user(user)
                .userStatus(UserStatus.ONLINE)
                .build();


        final UserServer saved = userServerRepository.save(userServer);
        userServerRepository.updateUserServer(serverId, userId, UserStatus.OFFLINE);
        userServerRepository.flush();

        final UserServer updated = userServerRepository.findById(saved.getId()).get();


        assertEquals(UserStatus.OFFLINE, updated.getUserStatus());


    }

}