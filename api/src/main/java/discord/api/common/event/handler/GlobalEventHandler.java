package discord.api.common.event.handler;

import discord.api.common.event.OnSignUpCompleteEvent;
import discord.api.common.exception.RestApiException;
import discord.api.entity.VerificationToken;
import discord.api.service.AuthService;
import discord.api.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GlobalEventHandler {
    private final MailService mailService;
    private final AuthService authService;

    /**
     * 회원가입 완료시 이메일 인증 메일 전송
     *
     * @param event : 회원가입 완료 이벤트
     * @author Jae Wook Jeong
     */
    @EventListener
    @Transactional
    public void onSignUpCompleteListener(OnSignUpCompleteEvent event) {
        System.out.println("EventHandler.onSignUpCompleteEvent() working");

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = authService.createVerificationToken(event.getUser(), token);
        mailService.sendVerificationMail(event.getUser().getEmail(), verificationToken);
    }
}
