package discord.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
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
     * S3에서 uuid list를 통해서 파일 다운로드
     *
     * @param uuidList : 다운로드할 파일의 UUID 리스트
     * @return s3ObjectList : S3에서 다운로드한 파일 리스트
     * @throws AmazonS3Exception : S3에서 파일 다운로드 실패 시 예외 발생
     * @author Jae Wook Jeong
     */
    public List<S3Object> downloadMultipartFileList(List<UUID> uuidList) throws AmazonS3Exception {
        List<S3Object> s3ObjectList = new ArrayList<>();

        for (UUID uuid : uuidList) {
            try {
                S3Object s3Object = amazonS3.getObject(bucket, uuid.toString());
                s3ObjectList.add(s3Object);
            } catch (AmazonS3Exception e) {
                throw new RestApiException(ErrorCode.AWS_S3_DOWNLOAD_FAIL);
            }
        }

        return s3ObjectList;
    }
}
