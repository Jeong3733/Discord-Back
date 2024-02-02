package discord.api.controller;

import discord.api.common.utils.AuthUtils;
import discord.api.dtos.user.EmailNicknameProfileDto;
import discord.api.entity.User;
import discord.api.dtos.auth.EmailRequestDto;
import discord.api.entity.enums.FriendshipRequestStatus;
import discord.api.entity.enums.FriendshipStatus;
import discord.api.service.FriendService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final AuthUtils authUtils;

    /**
     * 친구 신청 처리
     * @param receiver : 친구 요청을 받을 사람의 이메일
     * @return 친구 요청 상태
     *      성공적으로 보내짐 : PENDING
     *      친구 요청을 받은 사람이 이전에 친구 요청을 보낸 사람이라면 친구 요청을 수락함 or 이미 친구 : ACCEPTED
     *      차단된 상태 : BLOCKED
     * @author Jae Wook Jeong
     */
    @PostMapping("/friend/request")
    public ResponseEntity<FriendshipStatus> sendFriendRequest(final @RequestBody EmailRequestDto receiver, Authentication authentication) {
        User sender = authUtils.getUserFromAuthentication(authentication);
        FriendshipStatus friendshipStatus = friendService.sendFriendRequest(sender.getEmail(), receiver.getEmail());
        return ResponseEntity
                .ok()
                .body(friendshipStatus);
    }

    /**
     * 친구 요청 수락
     * @param sender : 친구 요청을 이전에 보냈던 사람
     * @author Jae Wook Jeong
     */
    @PostMapping("/friend/accept")
    public ResponseEntity<?> acceptFriendRequest(final @RequestBody EmailRequestDto sender, Authentication authentication) {
        // 친구 요청을 받았던 사람
        User receiver = authUtils.getUserFromAuthentication(authentication);
        friendService.changeFriendshipStatus(sender.getEmail(), receiver.getEmail(), FriendshipStatus.ACCEPTED);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 요청 거절
     * @param sender : 친구 요청을 이전에 보냈던 사람
     * @author Jae Wook Jeong
     */
    @PostMapping("/friend/decline")
    public ResponseEntity<?> declineFriendRequest(final @RequestBody EmailRequestDto sender, Authentication authentication) {
        // 친구 요청을 받았던 사람
        User receiver = authUtils.getUserFromAuthentication(authentication);
        friendService.changeFriendshipStatus(sender.getEmail(), receiver.getEmail(), FriendshipStatus.DECLINED);
        return ResponseEntity.ok().build();
    }

    /**
     * 차단
     * @param receiver : 차단당할 사람
     * @author Jae Wook Jeong
     */
    @PostMapping("/friend/block")
    public ResponseEntity<?> blockFriendRequest(final @RequestBody EmailRequestDto receiver, Authentication authentication) {
        // 차단을 요청한 사람
        User sender = authUtils.getUserFromAuthentication(authentication);
        friendService.changeFriendshipStatus(sender.getEmail(), receiver.getEmail(), FriendshipStatus.BLOCKED);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 리스트 (닉네임, 프로필 사진) 조회
     * @param pageable : 페이지 정보
     * @author Jae Wook Jeong
     */
    @GetMapping("/list/friend")
    public ResponseEntity<Page<EmailNicknameProfileDto>> getFriendList(final Pageable pageable, Authentication authentication) {
        User user = authUtils.getUserFromAuthentication(authentication);
        Page<EmailNicknameProfileDto> friendNicknameNProfileList =
                friendService.getFriendNicknameNProfileList(user.getEmail(), FriendshipRequestStatus.BOTH, FriendshipStatus.ACCEPTED, pageable);

        return ResponseEntity
                .ok()
                .body(friendNicknameNProfileList);
    }

    @GetMapping("/friend/request/received")
    public ResponseEntity<Page<EmailNicknameProfileDto>> getFriendRequestReceivedList(final Pageable pageable, Authentication authentication) {
        User user = authUtils.getUserFromAuthentication(authentication);
        Page<EmailNicknameProfileDto> friendNicknameNProfileList =
                friendService.getFriendNicknameNProfileList(user.getEmail(), FriendshipRequestStatus.RECEIVER, FriendshipStatus.PENDING, pageable);

        return ResponseEntity
                .ok()
                .body(friendNicknameNProfileList);
    }

    @GetMapping("/friend/request/send")
    public ResponseEntity<Page<EmailNicknameProfileDto>> getFriendRequestSendList(final Pageable pageable, Authentication authentication) {
        User user = authUtils.getUserFromAuthentication(authentication);
        Page<EmailNicknameProfileDto> friendNicknameNProfileList =
                friendService.getFriendNicknameNProfileList(user.getEmail(), FriendshipRequestStatus.SENDER, FriendshipStatus.PENDING, pageable);

        return ResponseEntity
                .ok()
                .body(friendNicknameNProfileList);
    }
}
