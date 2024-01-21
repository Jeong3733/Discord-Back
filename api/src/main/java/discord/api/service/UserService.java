package discord.api.service;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.entity.User;
import discord.api.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    /**
     * 이메일로 User 정보 가져오기
     *
     * @param email : 사용자 이메일
     * @return User : 사용자 정보
     * @throws RestApiException : 해당하는 이메일에 사용자가 존재하지 않을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new RestApiException(ErrorCode.EMAIL_NOT_FOUND);
                });
    }

    /**
     * id로 User 정보 가져오기
     *
     * @param id : 사용자 id
     * @return User : 사용자 정보
     * @throws RestApiException : 해당하는 id에 사용자가 존재하지 않을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new RestApiException(ErrorCode.USER_NOT_FOUND);
                });
    }
}
