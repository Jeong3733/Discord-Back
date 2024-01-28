package discord.api.repository.server;

import com.querydsl.jpa.impl.JPAQueryFactory;
import discord.api.entity.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static discord.api.entity.QServer.server;


@Repository
@RequiredArgsConstructor
public class ServerRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public List<Server> getServerListByUserId(Long id) {
        return queryFactory
                .selectFrom(server)
                .fetch();
    }
}
