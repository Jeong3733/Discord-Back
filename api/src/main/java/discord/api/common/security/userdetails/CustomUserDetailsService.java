package discord.api.common.security.userdetails;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.entity.User;
import discord.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(ErrorCode.EMAIL_NOT_FOUND));

        if (user != null)
            return new CustomUserDetails(user);

        return null;
    }
}
