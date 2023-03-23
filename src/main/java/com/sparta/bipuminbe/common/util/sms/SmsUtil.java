package com.sparta.bipuminbe.common.util.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.common.dto.SmsTo;
import com.sparta.bipuminbe.common.dto.SmsUtilDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

    public void sendMail(String content, List<String> toPhoneList) throws NoSuchAlgorithmException, InvalidKeyException {
        RestTemplate rest = new RestTemplate();
        rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();

        String timestamp = Long.toString(Instant.now().toEpochMilli());

        headers.add("Content-Type", "application/json; charset=utf-8");
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

    public String makeSignature(String timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method
        String url = "/sms/v2/services/" + serviceId + "/messages";    // url (include query string)
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

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        log.info("SMS makeSignature Success");
        System.out.println(encodeBase64String);
        return encodeBase64String;
    }
}
