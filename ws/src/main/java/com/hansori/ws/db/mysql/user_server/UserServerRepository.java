package com.hansori.ws.db.mysql.user_server;

import com.hansori.ws.db.mysql.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserServerRepository extends JpaRepository<UserServer, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update UserServer us set us.userStatus = :status where us.server.id = :serverId and us.user.id = :userId")
    void updateUserServer(@Param("serverId") long serverId, @Param("userId") long userId, @Param("status") UserStatus status);

}
