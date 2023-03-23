package com.sparta.bipuminbe.common.util.sms;

import com.sparta.bipuminbe.common.dto.SmsUtilDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class SmsUtil {

    @Value("${Naver.Cloud.Access}")
    private String naverCloudAccessKey;
    @Value("${Naver.Cloud.Secret}")
    private String naverCloudSecretKey;
    @Value("${Naver.Cloud.SmsServiceId}")
    private String serviceId;
    @Value("${fromPhone}")
    private String fromPhone;

    public void sendMail(String content, List<String> toPhoneList) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        String timestamp = Long.toString(Instant.now().toEpochMilli());
        headers.add("x-ncp-apigw-timestamp", timestamp);
        headers.add("x-ncp-iam-access-key", naverCloudAccessKey);
        headers.add("x-ncp-apigw-signature-v2", makeSignature(timestamp));

        SmsUtilDto body = SmsUtilDto.of(content, fromPhone, toPhoneList);

        HttpEntity<SmsUtilDto> requestEntity = new HttpEntity<SmsUtilDto>(body, headers);
        String url = "https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages";
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity, String.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        log.info("NAVER CLOUD API Status Code : " + status);
    }

    public String makeSignature(String timestamp) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method
        String url = "/services/" + serviceId + "/messages";    // url (include query string)
//        String timestamp = epoch;            // current timestamp (epoch)
        String accessKey = naverCloudAccessKey;            // access key id (from portal or Sub Account)
        String secretKey = naverCloudSecretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        log.info("SMS makeSignature Success");
        return encodeBase64String;
    }
}
