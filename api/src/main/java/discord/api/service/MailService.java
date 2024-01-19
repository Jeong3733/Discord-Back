package discord.api.service;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.entity.User;
import discord.api.entity.VerificationToken;
import discord.api.repository.UserRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${app.url.server}")
    private String serverUrl;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;

    /**
     * 인증 메일 전송
     *
     * @param email : 인증을 요청한 이메일
     * @param verifyToken : 생성된 Verification Token 정보
     * @throws RestApiException : 이메일 전송 실패, 이메일을 찾을 수 없을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public void sendVerificationMail(String email, VerificationToken verifyToken) {
        MimeMessage message = javaMailSender.createMimeMessage();

        // TODO: QueryDSL 을 사용해서 닉네임만 가져오기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("MailService.sendVerificationMail()");
                    throw new RestApiException(ErrorCode.EMAIL_NOT_FOUND);
                });

        String nickname = user.getNickname();
        String verificationHtml = setVerificationMail(nickname, verifyToken);

        try {
            message.setSubject("Discord 인증 메일입니다.");
            message.setText(verificationHtml, "UTF-8", "html");
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("MailService.sendVerificationMail()");
            throw new RestApiException(ErrorCode.MAIL_SEND_FAIL);
        }
    }

    /**
     * TemplateEngine 을 사용해서 인증 메일 HTML 생성
     *
     * @param nickname : 인증을 요청한 유저의 닉네임
     * @param verifyToken : 생성된 Verification Token 정보
     * @return String : 인증 메일 HTML 코드
     * @author Jae Wook Jeong
     */
    private String setVerificationMail(String nickname, VerificationToken verifyToken) {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("verificationUrl", serverUrl + "/verify?token=" + verifyToken.getToken());
        return templateEngine.process("verificationMail", context);
    }
}
