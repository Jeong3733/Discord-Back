package discord.api.repository;

import discord.api.entity.connectionEntity.UserServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserServerRepository extends JpaRepository<UserServer, Long> {
}
