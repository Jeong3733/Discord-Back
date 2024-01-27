package discord.api.repository.FriendshipRequest;

import discord.api.config.TestQueryDslConfig;
import discord.api.entity.User;
import discord.api.entity.connectionEntity.FriendshipRequest;
import discord.api.entity.dtos.user.NicknameNProfileIImgDto;
import discord.api.entity.enums.FriendshipStatus;
import discord.api.repository.User.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDslConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FriendShipRequestRepositoryCustomTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRequestRepository friendshipRequestRepository;

    @Autowired
    private FriendShipRequestRepositoryCustom friendShipRequestRepositoryCustom;

    private User user1, user2, user3, user4;

    @BeforeAll
    public void setUp() {
        user1 = User.builder()
                .email("user1@email.com")
                .nickname("user1")
                .build();

        user2 = User.builder()
                .email("user2@email.com")
                .nickname("user2")
                .build();

        user3 = User.builder()
                .email("user3@email.com")
                .nickname("user3")
                .build();

        user4 = User.builder()
                .email("user4@email.com")
                .nickname("user4")
                .build();

        userRepository.saveAll(List.of(user1, user2, user3, user4));


        FriendshipRequest friendshipRequest1 = FriendshipRequest.builder()
                .sender(user1)
                .receiver(user2)
                .status(FriendshipStatus.PENDING)
                .build();

        FriendshipRequest friendshipRequest2 = FriendshipRequest.builder()
                .sender(user1)
                .receiver(user3)
                .status(FriendshipStatus.ACCEPTED)
                .build();

        FriendshipRequest friendshipRequest3 = FriendshipRequest.builder()
                .sender(user1)
                .receiver(user4)
                .status(FriendshipStatus.DECLINED)
                .build();

        FriendshipRequest friendshipRequest4 = FriendshipRequest.builder()
                .sender(user2)
                .receiver(user3)
                .status(FriendshipStatus.BLOCKED)
                .build();

        FriendshipRequest friendshipRequest5 = FriendshipRequest.builder()
                .sender(user3)
                .receiver(user4)
                .status(FriendshipStatus.ACCEPTED)
                .build();

        friendshipRequestRepository.saveAll(List.of(friendshipRequest1, friendshipRequest2, friendshipRequest3, friendshipRequest4, friendshipRequest5));
    }

    @Test
    void getFriendInfoList() {
        Pageable pageable = PageRequest.of(0, 10); // 첫 페이지, 페이지 당 10개 항목

        final List<NicknameNProfileIImgDto> friendInfoList = friendShipRequestRepositoryCustom.getFriendInfoList(user1.getEmail(), pageable).getContent();

        assertEquals(1, friendInfoList.size());
        assertEquals("user3", friendInfoList.get(0).getNickname());

        // 테스트 실행
        final Page<NicknameNProfileIImgDto> result = friendShipRequestRepositoryCustom.getFriendInfoList(user4.getEmail(), pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("user3", result.getContent().get(0).getNickname());

        // 페이지 정보 검증
        assertEquals(0, result.getNumber()); // 현재 페이지
        assertEquals(10, result.getSize()); // 페이지 크기

        // 결과 검증
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertTrue(result.stream().anyMatch(dto -> dto.getNickname().equals(user2.getNickname())));
//        assertTrue(result.stream().anyMatch(dto -> dto.getNickname().equals(user3.getNickname())));
//        assertTrue(result.stream().anyMatch(dto -> dto.getNickname().equals(user4.getNickname())));

    }

    @Test
    void getBidirectionalFriendship() {
        final FriendshipRequest friendshipRequest1 = friendShipRequestRepositoryCustom.getBidirectionalFriendship(user1.getEmail(), user2.getEmail());
        assertNotNull(friendshipRequest1);
        assertEquals(user1.getEmail(), friendshipRequest1.getSender().getEmail());
        assertEquals(FriendshipStatus.PENDING, friendshipRequest1.getStatus());

        final FriendshipRequest friendshipRequest2 = friendShipRequestRepositoryCustom.getBidirectionalFriendship(user2.getEmail(), user1.getEmail());
        assertNotNull(friendshipRequest2);
        assertEquals(user2.getEmail(), friendshipRequest2.getReceiver().getEmail());
        assertEquals(FriendshipStatus.PENDING, friendshipRequest1.getStatus());

    }

    @Test
    void getFriendship() {
        final FriendshipRequest friendshipRequest1 = friendShipRequestRepositoryCustom.getFriendship(user1.getEmail(), user2.getEmail());
        assertNotNull(friendshipRequest1);
        assertEquals(user1.getEmail(), friendshipRequest1.getSender().getEmail());
        assertEquals(FriendshipStatus.PENDING, friendshipRequest1.getStatus());

        final FriendshipRequest friendshipRequest2 = friendShipRequestRepositoryCustom.getFriendship(user2.getEmail(), user1.getEmail());
        assertNull(friendshipRequest2);
    }
}