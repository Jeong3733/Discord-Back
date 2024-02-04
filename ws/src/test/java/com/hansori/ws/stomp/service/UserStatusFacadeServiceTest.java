package com.hansori.ws.stomp.service;

import com.hansori.ws.db.mysql.user.UserRepository;
import com.hansori.ws.db.mysql.user.UserStatus;
import com.hansori.ws.db.mysql.user_server.UserServerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserStatusFacadeServiceTest {

    @InjectMocks
    private  UserStatusFacadeService target;

    @Mock
    private UserServerRepository userServerRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void updateStatus() {

        final long roomId = 1L;
        final long userId = 1L;
        final UserStatus status = UserStatus.ONLINE;


        final ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<Long> roomIdCaptor = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<UserStatus> statusCaptor1 = ArgumentCaptor.forClass(UserStatus.class);
        final ArgumentCaptor<UserStatus> statusCaptor2 = ArgumentCaptor.forClass(UserStatus.class);


        target.updateUserStatus(roomId, status);

        BDDMockito.then(userRepository).should(Mockito.times(1))
                .updateUserStatus(userIdCaptor.capture(), statusCaptor1.capture());

        BDDMockito.then(userServerRepository).should(Mockito.times(1))
                .updateUserServer(roomIdCaptor.capture(), userIdCaptor.capture(), statusCaptor2.capture());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(roomId, roomIdCaptor.getValue());
        assertEquals(status, statusCaptor1.getValue());
        assertEquals(status, statusCaptor2.getValue());
    }

}