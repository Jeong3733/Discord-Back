package com.hansori.ws.stomp.common.utils;

import com.amazonaws.util.IOUtils;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public final class ImageUtils {

    public static String createFileName() {
        return UUID.randomUUID().toString();
    }

    public static ByteArrayInputStream ImageCodeToInputStream(final String imageCode) {

        final Base64.Decoder decoder = Base64.getDecoder();
        final byte[] imageBytes = decoder.decode(imageCode);
        return new ByteArrayInputStream(imageBytes);
    }

    public static String inputStreamToBase64(final InputStream inputStream) {
        try (inputStream){
            final byte[] byteArray = IOUtils.toByteArray(inputStream);
            final Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(byteArray);
        } catch (IOException e) {
            log.error("S3ObjectToByteArray error: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
