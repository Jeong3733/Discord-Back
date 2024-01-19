package discord.api.controller;

import discord.api.entity.dtos.AddServerDto;
import discord.api.service.AwsService;
import discord.api.service.ServerService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;
    private final AwsService awsService;

    /**
     * 서버 저장
     *
     * @param serverImage : 서버 프로필 이미지
     * @param addServerDto : 서버 정보
     * @param emailList : 서버에 초대할 유저의 이메일 리스트
     * @throws IOException : 파일을 찾을 수 없을 시 예외 발생
     * @return String : S3에 업로드된 파일의 UUID
     * @author Jae Wook Jeong
     */
    @PostMapping(value = "/server", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> madeServer(
            @RequestPart(value = "serverImage") @Nullable MultipartFile serverImage,
            @RequestPart(value = "serverInfo") AddServerDto addServerDto,
            @RequestPart(value = "friendList") @Nullable List<String> emailList)
            throws IOException {

        UUID uuid = null;
        if (serverImage != null && !serverImage.isEmpty()) {
            uuid = awsService.uploadMultipartFile(serverImage);
        }

        serverService.addServer(addServerDto, uuid, emailList);
        return new ResponseEntity<>(uuid != null ? uuid.toString() : null, HttpStatus.OK);
    }

}
