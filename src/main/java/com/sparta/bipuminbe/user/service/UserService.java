package com.sparta.bipuminbe.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.user.dto.KakaoUserInfoDto;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.jwt.JwtUtil;
import com.sparta.bipuminbe.user.dto.LoginRequestDto;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    @Value("${kakao.restapi.key}")
    private String apiKey;

    @Value("${kakao.redirect.url}")
    private String redirectUrl;



    //code -> 인가코드. 카카오에서 Param으로 넘겨준다.
    public ResponseEntity<ResponseDto<Boolean>> kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);


        // 4. JWT 토큰 반환

        HttpHeaders responseHeader = new HttpHeaders();
        String createToken = jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());
        responseHeader.add(JwtUtil.AUTHORIZATION_HEADER, createToken);

        Boolean isInput = kakaoUser.getDepartment() != null && kakaoUser.getEmpName() != null;

        return ResponseEntity.ok()
                .headers(responseHeader)
                .body(ResponseDto.success(isInput));
    }


//     1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", apiKey); // 본인의 발급받은 API 키 넣기
        body.add("redirect_uri", redirectUrl);

//        https://bipum-in.shop/api/user/kakao/callback
//        http://localhost:8080/api/user/kakao/callback
//        http://localhost:3000/api/user/kakao/callback
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); // 받은 것을 Json형태로 파싱
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String username = jsonNode.get("kakao_account") //이메일은 username으로 사용
                .get("email").asText();
        String profileImage = jsonNode.get("properties")
                .get("profile_image").asText();

        log.info("카카오 사용자 정보: " + id + ", " + username + ", " + profileImage);

        return KakaoUserInfoDto.builder()
                .id(id).username(username).image(profileImage).build();
    }


    // 3. 필요시에 회원가입
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {//받은 사용자 정보를 DTO로 받아옴
        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoUserInfo.getId()).orElse(null);

        if (kakaoUser == null) { // 유저가 없으면 새로 회원가입.
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            //Username이 이메일이므로 주의
            kakaoUser = User.builder().
                    kakaoId(kakaoUserInfo.getId()).
                    encodedPassword(encodedPassword).
                    kakaoUserInfoDto(kakaoUserInfo).
                    role(UserRoleEnum.USER).
                    alarm(true).
                    build();

            userRepository.save(kakaoUser);
        }

        return kakaoUser;
    }

    public void loginAdd(LoginRequestDto loginRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

    }
}