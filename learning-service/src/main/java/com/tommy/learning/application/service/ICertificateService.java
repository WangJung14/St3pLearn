package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.IssueCertificateRequest;
import com.tommy.learning.application.dto.response.CertificateResponse;
import com.tommy.learning.application.dto.response.VerifyCertificateResponse;

import java.util.UUID;

public interface ICertificateService {
    CertificateResponse issueCertificate(UUID instructorId, IssueCertificateRequest request);
    byte[] downloadCertificate(UUID studentId, UUID certificateId);
    VerifyCertificateResponse verifyCertificate(String certificateCode);
}
