package com.hansori.ws.stomp.service;

import com.hansori.ws.db.mysql.user.UserRepository;
import com.hansori.ws.db.mysql.user.UserStatus;
import com.hansori.ws.db.mysql.user_server.UserServerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusFacadeService {


    private final UserServerRepository userServerRepository;
    private final UserRepository userRepository;


    @Transactional
    public void updateUserServerStatus(final long roomId, final long userId, final UserStatus status) {
        userServerRepository.updateUserServer(roomId, userId, status);
    }

    @Transactional
    public void updateUserStatus(final long userId, final UserStatus status) {
        userRepository.updateUserStatus(userId, status);
        if(status == UserStatus.OFFLINE) userServerRepository.updateUserServer(userId, status);

    }

}
