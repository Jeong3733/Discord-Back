package discord.api.common.utils;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FileUtils {
    /**
     * S3 object 를 바이트 배열로 변환
     *
     * @param fileList : S3에 업로드된 파일 리스트
     * @return Map<UUID, byte[]> : S3에 업로드된 파일의 UUID, 파일의 바이트 배열
     * @throws RestApiException : 파일 변환시 예외 발생
     * @author Jae Wook Jeong
     */
    public Map<UUID, byte[]> mapS3ObjectsToByteArrays(List<S3Object> fileList) {
        return fileList.stream()
                .collect(Collectors.toMap(
                        s3Object -> UUID.fromString(s3Object.getKey()),
                        s3Object -> {
                            try {
                                return IOUtils.toByteArray(s3Object.getObjectContent());
                            } catch (IOException e) {
                                throw new RestApiException(ErrorCode.FILE_PROCESSING_FAIL);
                            }
                        }
                ));
    }

    public byte[] s3ObjectToByteArray(S3Object file) {
        try {
            return IOUtils.toByteArray(file.getObjectContent());
        } catch (IOException e) {
            throw new RestApiException(ErrorCode.FILE_PROCESSING_FAIL);
        }
    }
}
