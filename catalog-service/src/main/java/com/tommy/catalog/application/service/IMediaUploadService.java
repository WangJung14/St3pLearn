package com.tommy.catalog.application.service;

import java.util.Map;

public interface IMediaUploadService {
    Map<String, Object> generateCloudinarySignature();
}
