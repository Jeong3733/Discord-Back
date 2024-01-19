package discord.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import discord.api.common.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    /**
     * 파일 S3에 업로드
     *
     * @param file : 업로드할 파일
     * @throws AmazonS3Exception : S3에 파일 업로드 실패 시 예외 발생
     * @throws IOException : 파일을 찾을 수 없을 시 예외 발생
     * @return UUID : S3에 업로드된 파일의 UUID
     * @author Jae Wook Jeong
     */
    public UUID uploadMultipartFile(MultipartFile file) throws AmazonS3Exception, IOException {
        UUID uuid = UUID.randomUUID();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucket,
                uuid.toString(),
                file.getInputStream(),
                objectMetadata
        );

        amazonS3.putObject(putObjectRequest);
        log.info("AWS S3 파일 업로드 완료");
        return uuid;
    }
}
