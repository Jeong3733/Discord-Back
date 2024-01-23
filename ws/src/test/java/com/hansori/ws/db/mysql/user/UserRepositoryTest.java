package com.hansori.ws.db.mysql.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void getNicknameByUserId() {

        final User user = User.builder()
                .nickname("test")
                .build();

        final User saved = userRepository.save(user);

        final String nickname = userRepository.getNicknameByUserId(saved.getId());

        assertEquals(user.getNickname(), nickname);
    }

    @Test
    void updateUserStatusToOnline() {

        final User user = User.builder()
                .userStatus(UserStatus.OFFLINE)
                .build();

        final User saved = userRepository.save(user);

        userRepository.updateUserStatus(saved.getId(), UserStatus.ONLINE);

        assertEquals(UserStatus.ONLINE, userRepository.findById(saved.getId()).get().getUserStatus());
    }

    @Test
    void updateUserStatusToOffline() {

        final User user = User.builder()
                .userStatus(UserStatus.ONLINE)
                .build();

        final User saved = userRepository.save(user);

        userRepository.updateUserStatus(saved.getId(), UserStatus.OFFLINE);

        assertEquals(UserStatus.OFFLINE, userRepository.findById(saved.getId()).get().getUserStatus());
    }


}