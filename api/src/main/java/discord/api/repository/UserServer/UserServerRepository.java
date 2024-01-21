package discord.api.repository.UserServer;

import discord.api.entity.connectionEntity.UserServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserServerRepository extends JpaRepository<UserServer, Long> {
}
