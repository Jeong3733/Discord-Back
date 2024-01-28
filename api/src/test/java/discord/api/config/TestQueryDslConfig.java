package discord.api.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import discord.api.repository.FriendshipRequest.FriendShipRequestRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestQueryDslConfig {
    @PersistenceContext
    private EntityManager em;
    
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public FriendShipRequestRepositoryCustom friendShipRequestRepositoryCustom() {
        return new FriendShipRequestRepositoryCustom(jpaQueryFactory());
    }
}
