package com.hansori.ws.db.mysql.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u.nickname from User u where u.id = :userId")
    String getNicknameByUserId(long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.userStatus = :status where u.id = :userId")
    void updateUserStatus(@Param("userId") long userId, @Param("status") UserStatus status);

    @Query("select u.id, u.nickname from User u join UserServer us on u.id = us.user.id " +
            "where us.server.id = :serverId")
    List<Object[]> getUserListByServerId(@Param("serverId") long serverId);
}
