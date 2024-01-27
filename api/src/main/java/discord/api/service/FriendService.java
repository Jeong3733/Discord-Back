package discord.api.service;

import com.amazonaws.services.s3.model.S3Object;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.common.utils.FileUtils;
import discord.api.entity.User;
import discord.api.entity.connectionEntity.FriendshipRequest;
import discord.api.dtos.user.NicknameNProfileIImgDto;
import discord.api.entity.enums.FriendshipStatus;
import discord.api.repository.FriendshipRequest.FriendShipRequestRepositoryCustom;
import discord.api.repository.FriendshipRequest.FriendshipRequestRepository;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final FriendShipRequestRepositoryCustom friendShipRequestRepositoryCustom;
    private final UserService userService;
    private final AwsService awsService;
    private final FileUtils fileUtils;

    /**
     * 친구 신청 저장
     * 1. 친구 요청을 받은 사람이 이전에 친구 요청을 보낸 사람이라면 친구 요청을 수락
     * 2. 이미 친구
     * 3. 서로 처음 요청이라면 친구 요청을 저장
     * 4. 차단된 상태
     *
     * @param senderEmail : 보내는 사람 이메일
     * @param receiverEmail : 받는 사람 이메일
     * @return 친구 요청 상태
     *      성공적으로 보내짐 : PENDING
     *      친구 요청을 받은 사람이 이전에 친구 요청을 보낸 사람이라면 친구 요청을 수락함 or 이미 친구 : ACCEPTED
     *      차단된 상태 : BLOCKED
     * @author Jae Wook Jeong
     */
    @Transactional
    public FriendshipStatus sendFriendRequest(String senderEmail, String receiverEmail) {

        // TODO : receiver나 sender가 존재하지 않는다면 예외 발생
        // TODO : receiver / sender가 같을 시 예외 발생

        FriendshipRequest friendshipRequest =
                friendShipRequestRepositoryCustom.getBidirectionalFriendship(senderEmail, receiverEmail);

        if (friendshipRequest != null) {
            FriendshipStatus status = friendshipRequest.getStatus();

            switch (status) {
                case PENDING -> {
                    // 친구 요청을 받은 사람이 이전에 친구 요청을 보낸 사람이라면 친구 요청을 수락한다.
                    if (friendshipRequest.getReceiver().getEmail().equals(senderEmail)) {
                        friendshipRequest.accept();
                        return FriendshipStatus.ACCEPTED;
                    }

                    friendshipRequest.update();
                    return FriendshipStatus.PENDING;
                }

                case ACCEPTED -> {
                    return FriendshipStatus.ACCEPTED;
                }

                case BLOCKED -> {
                    return FriendshipStatus.BLOCKED;
                }

                case DECLINED -> {
                    friendshipRequest.pending();
                    return FriendshipStatus.PENDING;
                }
            }
        }

        saveFriendshipRequest(senderEmail, receiverEmail, FriendshipStatus.PENDING);
        return FriendshipStatus.PENDING;
    }

    /**
     * 친구 관계 변화
     *
     * @param senderEmail : 보내는 사람 이메일
     * @param receiverEmail : 받는 사람 이메일
     * @throws RestApiException : 친구 요청이 없을 경우 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public void changeFriendshipStatus(String senderEmail, String receiverEmail, FriendshipStatus status) {
       FriendshipRequest friendshipRequest =
                friendShipRequestRepositoryCustom.getFriendship(senderEmail, receiverEmail);

       // TODO : 만약 없는 요청이라면 예외 발생
        switch (status) {
            case PENDING, ACCEPTED -> friendshipRequest.accept();
            case DECLINED -> friendshipRequest.decline();
            case BLOCKED -> {

                // 이전에 차단을 당할 사람 (receiverEmail)이
                // 차단을 요청한 사람 (senderEmail)에게 친구 요청을 보낸적이 있다면
                // friendshipRequest 에 (sender = receiverEmail, receiver = senderEmail) 인 FriendshipRequest 가 존재한다.
                // 따라서 이 경우에는 FriendshipRequest 를 찾아서 block() 을 호출한다.
                FriendshipRequest bidirectionalFriendship =
                        friendShipRequestRepositoryCustom.getBidirectionalFriendship(senderEmail, receiverEmail);

                if (bidirectionalFriendship != null) {
                    bidirectionalFriendship.changeSenderReceiver();
                    bidirectionalFriendship.block();
                }

                else
                    saveFriendshipRequest(senderEmail, receiverEmail, FriendshipStatus.BLOCKED);
            }
            default -> throw new RestApiException(ErrorCode.FRIENDSHIP_REQUEST_NOT_FOUND);
        }
    }

    @Transactional
    public Page<NicknameNProfileIImgDto> getFriendNicknameNProfileList(String email, Pageable pageable) {
        Page<NicknameNProfileIImgDto> friendInfoList = friendShipRequestRepositoryCustom.getFriendInfoList(email, pageable);

        List<S3Object> s3ObjectList = friendInfoList.stream()
                        .map(NicknameNProfileIImgDto::getProfileImageId)
                        .filter(Objects::nonNull)
                        .map(awsService::downloadMultipartFile)
                    .toList();

        Map<UUID, byte[]> profileImageMap = fileUtils.mapS3ObjectsToByteArrays(s3ObjectList);

        List<NicknameNProfileIImgDto> updatedFriendInfoList = friendInfoList.stream()
                .peek(friendInfoDto -> {
                    UUID uuid = friendInfoDto.getProfileImageId();

                    String profileImage = profileImageMap.get(friendInfoDto.getProfileImageId()) != null ?
                            Base64.getEncoder().encodeToString(profileImageMap.get(uuid)) : null;

                    friendInfoDto.profileImage(profileImage);
                })
                .toList();

        return PageableExecutionUtils.getPage(updatedFriendInfoList, pageable, updatedFriendInfoList::size);
    }

    @Transactional
    protected void saveFriendshipRequest(String senderEmail, String receiverEmail, FriendshipStatus status) {
        User sender = userService.getUserByEmail(senderEmail);
        User receiver = userService.getUserByEmail(receiverEmail);

        FriendshipRequest newFriendshipRequest = FriendshipRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(status)
                .build();

        friendshipRequestRepository.save(newFriendshipRequest);
    }
}
