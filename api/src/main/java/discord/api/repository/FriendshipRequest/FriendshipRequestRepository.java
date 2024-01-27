package discord.api.repository.FriendshipRequest;

import discord.api.entity.User;
import discord.api.entity.connectionEntity.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {
}
