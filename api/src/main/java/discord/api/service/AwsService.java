package discord.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO : Exception 자세하게 처리하기
public class AwsService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    /**
     * 파일 S3에 업로드
     *
     * @param file : 업로드할 파일
     * @return UUID : S3에 업로드된 파일의 UUID
     * @throws AmazonS3Exception : S3에 파일 업로드 실패 시 예외 발생
     * @throws IOException       : 파일을 찾을 수 없을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public UUID uploadMultipartFile(MultipartFile file) throws IOException {
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

        try {
            amazonS3.putObject(putObjectRequest);
        } catch (AmazonS3Exception e) {
            throw new RestApiException(ErrorCode.AWS_S3_UPLOAD_FAIL);
        }
        return uuid;
    }

    /**
     * S3에서 uuid 를 통해서 파일 다운로드
     *
     * @param uuid : 다운로드할 파일의 UUID
     * @return S3Object : S3에서 다운로드한 파일 리스트
     * @throws AmazonS3Exception : S3에서 파일 다운로드 실패 시 예외 발생
     * @author Jae Wook Jeong
     */
    public S3Object downloadMultipartFile(UUID uuid) throws AmazonS3Exception {
        try {
            return amazonS3.getObject(bucket, uuid.toString());
        } catch (AmazonS3Exception e) {
            throw new RestApiException(ErrorCode.AWS_S3_DOWNLOAD_FAIL);
        }
    }


    /**
     * S3에서 uuid 를 통해서 파일 다운로드
     *
     * @param fileName : 다운로드할 파일의 이름
     * @return S3Object : S3에서 다운로드한 파일 리스트
     * @throws AmazonS3Exception : S3에서 파일 다운로드 실패 시 예외 발생
     * @throws IOException       : 파일을 찾을 수 없을 시 예외 발생
     * @author Baek Seung Jin
     */
    public String downloadFile(final String fileName) throws AmazonS3Exception {
        try(final S3Object s3Object = amazonS3.getObject(bucket, fileName)) {
            final S3ObjectInputStream objectContent = s3Object.getObjectContent();
            return FileUtils.inputStreamToBase64(objectContent);
        } catch (AmazonS3Exception | IOException e) {
            throw new RestApiException(ErrorCode.AWS_S3_DOWNLOAD_FAIL);
        }
    }


    public void deleteMultipartFile(UUID uuid) throws AmazonS3Exception {
        try {
            amazonS3.deleteObject(bucket, uuid.toString());
        } catch (AmazonS3Exception e) {
            throw new RestApiException(ErrorCode.AWS_S3_DOWNLOAD_FAIL);
        }
    }

}
