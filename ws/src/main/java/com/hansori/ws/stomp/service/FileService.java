package com.hansori.ws.stomp.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hansori.ws.stomp.common.utils.ImageUtils;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.hansori.ws.stomp.common.utils.ImageUtils.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String uploadFile(final String imageCode) {

        final String fileName = createFileName();
        try (final ByteArrayInputStream inputStream = ImageCodeToInputStream(imageCode)){
            final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, inputStream, new ObjectMetadata());
            amazonS3.putObject(putObjectRequest);
        }  catch (SdkClientException e) {
            log.error("uploadFile error: {}", e.getMessage());
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.error("uploadFile error: {}", e.getMessage());
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return fileName;
    }

    public String downloadFile(final String fileName) {
        try (final S3Object s3Object = amazonS3.getObject(bucket, fileName)){

            final S3ObjectInputStream objectContent = s3Object.getObjectContent();
            return ImageUtils.inputStreamToBase64(objectContent);
        }catch (Exception e) {

            log.error("downloadFile error: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }




}
