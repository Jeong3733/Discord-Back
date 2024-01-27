package discord.api.controller;

import discord.api.common.utils.AuthUtils;
import discord.api.entity.User;
import discord.api.entity.dtos.server.AddServerDto;
import discord.api.entity.dtos.server.ServerDto;
import discord.api.service.AwsService;
import discord.api.service.ServerService;
import discord.api.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;
    private final UserService userService;
    private final AwsService awsService;
    private final AuthUtils authUtils;

    /**
     * 서버 저장
     *
     * @param serverImage  : 서버 프로필 이미지
     * @param addServerDto : 서버 정보
     * @param emailList    : 서버에 초대할 유저의 이메일 리스트
     * @return String : S3에 업로드된 파일의 UUID
     * @throws IOException : 파일을 찾을 수 없을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @PostMapping(value = "/server", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> madeServer(
            @RequestPart(value = "serverImage") @Nullable final MultipartFile serverImage,
            @RequestPart(value = "serverInfo") final AddServerDto addServerDto,
            @RequestPart(value = "friendList") @Nullable final List<String> emailList,
            Authentication authentication)
            throws IOException {

        User user = authUtils.getUserFromAuthentication(authentication);

        String email = user.getEmail();
        emailList.add(email);

        UUID uuid = null;
        if (serverImage != null && !serverImage.isEmpty()) {
            uuid = awsService.uploadMultipartFile(serverImage);
        }

        serverService.addServer(addServerDto, uuid, emailList);
        return ResponseEntity
                .ok()
                .body(uuid != null ? uuid.toString() : null);
    }

    /**
     * 서버 목록 불러오기
     *
     * @param authentication : 사용자 정보
     * @return HashMap<String, List<ServerDto>> : 서버 리스트
     * @author Jae Wook Jeong
     */
    @PostMapping("/list/server")
    public ResponseEntity<HashMap<String, List<ServerDto>>> getServerList(Authentication authentication) {
        User user = authUtils.getUserFromAuthentication(authentication);
        Long userId = user.getId();

        List<ServerDto> serverDtoList = serverService.getServerList(userId);

        HashMap<String, List<ServerDto>> serverDtoListMap = new HashMap<>();
        serverDtoListMap.put("serverList", serverDtoList);

        return ResponseEntity
                .ok()
                .body(serverDtoListMap);
    }
}
