package discord.api.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.entity.Server;
import discord.api.entity.User;
import discord.api.entity.connectionEntity.UserServer;
import discord.api.entity.dtos.AddServerDto;
import discord.api.entity.enums.UserStatus;
import discord.api.repository.ServerRepository;
import discord.api.repository.UserRepository;
import discord.api.repository.UserServerRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final UserServerRepository userServerRepository;

    /**
     * 서버 저장
     *
     * @param addServerDto : 서버 정보
     * @param uuid : 업로드할 파일의 UUID
     * @param emailList : 서버에 초대할 유저의 이메일 리스트
     * @author Jae Wook Jeong
     */
    public void addServer(AddServerDto addServerDto, @Nullable UUID uuid, List<String> emailList) {
        Server server = Server.builder()
                .name(addServerDto.getName())
                .description(addServerDto.getDescription())
                .profileImage(uuid)
                .build();

        serverRepository.save(server);

        for (String email : emailList) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("ServerService.addServer()");
                        throw new RestApiException(ErrorCode.EMAIL_NOT_FOUND);
                    });

            UserServer userServer = UserServer.builder()
                    .server(server)
                    .user(user)
                    .userStatus(UserStatus.OFFLINE)
                    .build();

            userServerRepository.save(userServer);
        }
    }
}
