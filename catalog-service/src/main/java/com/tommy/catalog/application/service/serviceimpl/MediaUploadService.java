package com.tommy.catalog.application.service.serviceimpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tommy.catalog.application.service.IMediaUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MediaUploadService implements IMediaUploadService {
    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    // Called by the frontend to retrieve the signature before uploading.
    @Override
    public Map<String, Object> generateCloudinarySignature() {
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> paramsToSign = ObjectUtils.asMap(
                "timestamp", timestamp,
                "folder", "st3p-learn/lessons"
        );

        Cloudinary cloudinary = new Cloudinary();
        String signature = cloudinary.apiSignRequest(paramsToSign, apiSecret);

        return ObjectUtils.asMap(
                "signature", signature,
                "timestamp", timestamp,
                "api_key", apiKey,
                "folder", "st3p-learn/lessons"
        );
    }
}
