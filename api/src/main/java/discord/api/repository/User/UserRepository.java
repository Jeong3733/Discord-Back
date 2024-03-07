package discord.api.repository.User;

import discord.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);


    @Query("select u.id, u.nickname from User u inner join UserServer us on u.id = us.user.id " +
            "where us.server.id = :serverId")
    List<Object[]> getUserListByServerId(@Param("serverId") long serverId);
}
