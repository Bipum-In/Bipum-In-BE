package com.sparta.bipuminbe.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.*;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.s3.S3Uploader;
import com.sparta.bipuminbe.common.util.redis.RedisRepository;
import com.sparta.bipuminbe.common.util.redis.RefreshToken;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.dto.*;
import com.sparta.bipuminbe.common.jwt.JwtUtil;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final RequestsRepository requestsRepository;
    private final SupplyRepository supplyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final S3Uploader s3Uploader;
    private final RedisRepository redisRepository;

    //    @Value("${login.encrypt.algorithm}")
//    private final String alg;
//    @Value("${login.encrypt.key}")
//    private final String key;
//    @Value("${login.encrypt.iv}")
//    private final String iv;

//    @Value("${kakao.restapi.key}")
//    private String apiKey;
//    @Value("${kakao.redirect.local.url}")
//    private String redirectLocalUrl;
//    @Value("${kakao.redirect.server.url}")
//    private String redirectServerUrl;

    @Value("${google.auth.clientId}")
    private String clientId;
    @Value("${google.auth.client_secret}")
    private String clientSecret;
    @Value("${google.auth.local.redirect.url}")
    private String redirectLocalUrl;
    @Value("${google.auth.server.redirect.url}")
    private String redirectServerUrl;
    @Value("${google.auth.local.redirect.url.delete}")
    private String redirectLocalDeleteUrl;
    @Value("${google.auth.server.redirect.url.delete}")
    private String redirectServerDeleteUrl;

    @Transactional
    public ResponseEntity<ResponseDto<LoginResponseDto>> googleLogin(String code, String urlType, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        AccessTokenDto accessToken = getToken(code, urlType, GoogleTokenType.LOGIN);

        log.info("accessToken : " + accessToken.getAccess_token());

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기.
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
        log.info("구글 사용자 정보 : " + googleUserInfo.getId() + ", " + googleUserInfo.getEmail() + ", " + googleUserInfo.getName()
                + ", " + googleUserInfo.getPicture());
        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(googleUserInfo, accessToken);

        // 4. JWT 토큰 반환
        // Token 생성 Access/Refresh + addHeader
        HttpHeaders responseHeader = new HttpHeaders();
        String createdAccessToken = jwtUtil.createToken(googleUser.getUsername(), googleUser.getRole(), TokenType.ACCESS);
        String createdRefreshToken = jwtUtil.createToken(googleUser.getUsername(), googleUser.getRole(), TokenType.REFRESH);
        responseHeader.add(JwtUtil.AUTHORIZATION_HEADER, createdAccessToken);
        responseHeader.add(JwtUtil.REFRESH_HEADER, createdRefreshToken);

        Optional<RefreshToken> refreshToken = redisRepository.findById(googleUser.getUsername());
        long expiration = jwtUtil.REFRESH_TOKEN_TIME / 1000;    // ms -> seconds

        if (refreshToken.isPresent()) {
            RefreshToken savedRefreshToken = refreshToken.get().updateToken(createdRefreshToken, expiration);
            redisRepository.save(savedRefreshToken);
        } else {
            RefreshToken refreshToSave = RefreshToken.builder()
                    .username(googleUser.getUsername())
                    .ip(getClientIp(httpServletRequest))
                    .refreshToken(createdRefreshToken)
                    .expiration(expiration).build();
            redisRepository.save(refreshToSave);
        }

        Boolean checkUser = googleUser.getDepartment() != null && googleUser.getEmpName() != null
                && googleUser.getPhone() != null && googleUser.getPassword() != null;

        return ResponseEntity.ok()
                .headers(responseHeader)
                .body(ResponseDto.success(LoginResponseDto.of(googleUser, checkUser)));
    }


    public static String getClientIp(HttpServletRequest request) {
        String clientIp = null;
        boolean isIpInHeader = false;

        List<String> headerList = new ArrayList<>();
        headerList.add("X-Forwarded-For");
        headerList.add("HTTP_CLIENT_IP");
        headerList.add("HTTP_X_FORWARDED_FOR");
        headerList.add("HTTP_X_FORWARDED");
        headerList.add("HTTP_FORWARDED_FOR");
        headerList.add("HTTP_FORWARDED");
        headerList.add("Proxy-Client-IP");
        headerList.add("WL-Proxy-Client-IP");
        headerList.add("HTTP_VIA");
        headerList.add("IPV6_ADR");

        for (String header : headerList) {
            clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !clientIp.equals("unknown")) {
                isIpInHeader = true;
                break;
            }
        }

        if (!isIpInHeader) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }


    //     1. "인가 코드"로 "액세스 토큰" 요청
    private AccessTokenDto getToken(String code, String urlType, GoogleTokenType googleTokenType) throws JsonProcessingException {
        String redirectUrl = "";
        if(googleTokenType == GoogleTokenType.LOGIN){
            redirectUrl = urlType.equals("local") ? redirectLocalUrl : redirectServerUrl;
        }else {
            redirectUrl = urlType.equals("local") ? redirectLocalDeleteUrl : redirectServerDeleteUrl;
        }

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId); // 클라이언트 Id
        body.add("client_secret", clientSecret); // 클라이언트 Secret
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", "authorization_code");
//        body.add("access_type", "offline");
//        body.add("prompt", "consent");

        // HTTP 요청 보내기1
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        ObjectMapper objectMapper = new ObjectMapper(); // 받은 것을 Json형태로 파싱

//        jsonNode.get("id_token").asText(); // refresh 토큰
//        jsonNode.get("access_token").asText(); // 엑세스 토큰
        return objectMapper.readValue(response.getBody(), AccessTokenDto.class);
    }


    // 2. 토큰으로 구글 로그인 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
    private GoogleUserInfoDto getGoogleUserInfo(AccessTokenDto accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.getBody(), GoogleUserInfoDto.class);
    }


    // 3. 필요시에 회원가입
    private User registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfo, AccessTokenDto accessToken) {
        // DB 에 중복된 Google Id 가 있는지 확인
        User googleUser = userRepository.findByUsername(googleUserInfo.getEmail()).orElse(null);

        if (googleUser == null) { // 유저가 없으면 새로 회원가입..

            //Username이 이메일이므로 주의
            googleUser = User.builder().
                    googleId(googleUserInfo.getId()).
                    username(googleUserInfo.getEmail()).
                    image(googleUserInfo.getPicture()).
                    accessToken(accessToken.getAccess_token()).
//                    refreshToken(accessToken.getRefresh_token()).
                    role(UserRoleEnum.ADMIN).
                    alarm(true).
                    deleted(false).
                    build();

            userRepository.save(googleUser);
        }

        return googleUser;
    }


    @Transactional
    public ResponseDto<String> logout(String username) {
        deleteRefreshToken(username);
        return ResponseDto.success("로그아웃 성공");
    }

    @Transactional
    public ResponseDto<LoginResponseDto> loginAdd(LoginRequestDto loginRequestDto, User user) {
        User foundUser = getUser(user.getId());
        Department department = getDepartment(loginRequestDto.getDepartmentId());
        String encodedPassword = passwordEncoder.encode(loginRequestDto.getPassword());

        foundUser.update(loginRequestDto.getEmpName(), department, loginRequestDto.getPhone(),
                foundUser.getAlarm(), foundUser.getImage(), encodedPassword);
        Boolean checkUser = foundUser.getEmpName() == null || foundUser.getDepartment() == null
                || foundUser.getPhone() == null || encodedPassword == null;
        return ResponseDto.success(LoginResponseDto.of(foundUser, checkUser));
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<UserResponseDto>> getUserByDept(Long deptId) {
        List<User> userInDeptList = userRepository.findByDepartmentAndDeletedFalse(getDepartment(deptId));
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        for (User user : userInDeptList) {
            userResponseDtoList.add(UserResponseDto.of(user));
        }
        return ResponseDto.success(userResponseDtoList);
    }

    private Department getDepartment(Long deptId) {
        return departmentRepository.findByIdAndDeletedFalse(deptId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundDepartment));
    }


    // 구글과 연결된 계정 삭제
    @Transactional
    public ResponseDto<String> deleteUser(User user, String bearerToken, String code, String urlType) throws JsonProcessingException {
        // 유저에 있는 Access 토큰은 로그인 시에 생성된 Access 토큰이기 때문에, 삭제 시 갱신이 필요함. refresh 토큰 null 이슈로 인한 처리
        AccessTokenDto accessToken = getToken(code, urlType, GoogleTokenType.DELETE);

        //현재 유저에 있는 Access 토큰을 갱신된 토큰으로 교체
        user.refreshGoogleToken(accessToken.getAccess_token());

        // 구글 API와 통신을 통해 연결 끊기
        unlinkGoogleAPI(user, bearerToken);

        User googleUser = userRepository.findByGoogleIdAndDeletedFalse(user.getGoogleId()).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUser));

        deleteRefreshToken(googleUser.getUsername());

        returnSuppliesByDeletedUser(user, user);

        // DB의 회원정보 삭제
        userRepository.deleteByGoogleId(googleUser.getGoogleId());

        return ResponseDto.success("계정 연결 끊기 및 삭제 완료");
    }


    // 비품 자동 반납.
    public void returnSuppliesByDeletedUser(User user, User admin) {
        List<Supply> supplyList = supplyRepository.findByUser_IdAndDeletedFalse(user.getId());
        for (Supply supply : supplyList) {
            // 비품 자동 반납에 의한 기록 생성.
            requestsRepository.save(Requests.builder()
                    .requestType(RequestType.RETURN)
                    .content("유저 탈퇴에 의한 비품 자동 반납")
                    .acceptResult(AcceptResult.ACCEPT)
                    .requestStatus(RequestStatus.PROCESSED)
                    .supply(supply)
                    .useType(supply.getUseType())
                    .user(user)
                    .admin(admin)
                    .build());
            supply.returnSupply();
        }
    }


    @Transactional(readOnly = true)
    public ResponseDto<Map<String, Set<String>>> getAllUserList() {
        List<Department> departmentList = departmentRepository.findByDeletedFalse();
        List<User> userList = userRepository.findByDeletedFalse();

        // 2중 for문을 돌리는 것 보다 유리할 것 같아 하나 만들어줌. (dept와 번호를 연결하는 SetList.)
        Map<String, Integer> deptNumber = new HashMap<>();
        for (int i = 0; i < departmentList.size(); i++) {
            deptNumber.put(departmentList.get(i).getDeptName(), i);
        }

        // 배열 번호는 부서를 가리키게 된다.
        Set<String>[] userListByDept = new Set[departmentList.size()];
        // 초기화
        for (int i = 0; i < userListByDept.length; i++) {
            userListByDept[i] = new HashSet<>();
        }
        // 부서 이름에 맞는 배열에 사원명을 집어넣음.
        for (User user : userList) {
            String deptName = user.getDepartment().getDeptName();
            String empName = user.getEmpName();
            userListByDept[deptNumber.get(deptName)].add(empName);
        }

        // 부서명 : 사원 리스트
        Map<String, Set<String>> userMapByDept = new HashMap<>();
        for (Department department : departmentList) {
            String deptName = department.getDeptName();
            userMapByDept.put(deptName, userListByDept[deptNumber.get(deptName)]);
        }
        return ResponseDto.success(userMapByDept);
    }


    @Transactional(readOnly = true)
    public ResponseDto<UserInfoResponseDto> getUserInfo(User user) {
        // lazyInitialize 오류로 인해 user를 그대로 사용 못했음..
        User foundUser = getUser(user.getId());
        return ResponseDto.success(UserInfoResponseDto.of(foundUser));
    }

    private User getUser(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUser));
    }


    @Transactional
    public ResponseDto<String> updateUser(UserUpdateRequestDto userUpdateRequestDto, User user) throws IOException {
        String image = userUpdateRequestDto.getImage();
        if (image == null || image.equals("")) {
            image = s3Uploader.uploadFiles(userUpdateRequestDto.getMultipartFile(), "user");
        }

        User foundUser = getUser(user.getId());
        foundUser.update(userUpdateRequestDto.getEmpName(), getDepartment(userUpdateRequestDto.getDeptId()),
                userUpdateRequestDto.getPhone(), userUpdateRequestDto.getAlarm(), image, foundUser.getPassword());
        return ResponseDto.success("정보 수정 완료");
    }


    @Transactional(readOnly = true)
    public ResponseDto<String> reIssueAccessToken(User user, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        RefreshToken refreshToken = redisRepository.findById(user.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRefreshToken));
        if (!getClientIp(httpServletRequest).equals(refreshToken.getIp())) {
            redisRepository.deleteById(user.getUsername());
            throw new CustomException(ErrorCode.NotMatchedIp);
        }

        String accessToken = jwtUtil.createToken(user.getUsername(), user.getRole(), TokenType.ACCESS);
        httpServletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        return ResponseDto.success("토큰 재발급 완료.");
    }


    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDto<LoginResponseDto>> toyLogin(String username, HttpServletRequest httpServletRequest) {
        User googleUser = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.NotFoundUser));
        String createdAccessToken = jwtUtil.createToken(googleUser.getUsername(), googleUser.getRole(), TokenType.ACCESS);
        String createdRefreshToken = jwtUtil.createToken(googleUser.getUsername(), googleUser.getRole(), TokenType.REFRESH);
        // 나중에 제거 해줘야 함.
        log.info(getClientIp(httpServletRequest));
        // header에 올리기.
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add(JwtUtil.AUTHORIZATION_HEADER, createdAccessToken);
        responseHeader.add(JwtUtil.REFRESH_HEADER, createdRefreshToken);

        Optional<RefreshToken> refreshToken = redisRepository.findById(googleUser.getUsername());
        long expiration = jwtUtil.REFRESH_TOKEN_TIME / 1000;    // ms -> seconds

        if (refreshToken.isPresent()) {
            RefreshToken savedRefreshToken = refreshToken.get().updateToken(createdRefreshToken, expiration);
            redisRepository.save(savedRefreshToken);
        } else {
            RefreshToken refreshToSave = RefreshToken.builder()
                    .username(googleUser.getUsername())
                    .ip(getClientIp(httpServletRequest))
                    .refreshToken(createdRefreshToken)
                    .expiration(expiration).build();
            redisRepository.save(refreshToSave);
        }

        Boolean checkUser = googleUser.getDepartment() != null && googleUser.getEmpName() != null && googleUser.getPhone() != null;

        return ResponseEntity.ok()
                .headers(responseHeader)
                .body(ResponseDto.success(LoginResponseDto.of(googleUser, checkUser)));
    }


    @Transactional(readOnly = true)
    public ResponseDto<String> toyReissue(String username, String ip, HttpServletResponse httpServletResponse) {
        RefreshToken refreshToken = redisRepository.findById(username).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRefreshToken));
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundUser));

        if (!ip.equals(refreshToken.getIp())) {
            throw new CustomException(ErrorCode.NotMatchedIp);
        }

        String accessToken = jwtUtil.createToken(user.getUsername(), user.getRole(), TokenType.ACCESS);
        httpServletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        return ResponseDto.success("토큰 재발급 완료.");
    }


    @Transactional
    public ResponseDto<String> manageUser(Long userId, User admin) {
        User user = getUser(userId);
        returnSuppliesByDeletedUser(user, admin);
        userRepository.delete(user);
        return ResponseDto.success("유저 삭제 완료.");
    }

    @Transactional(readOnly = true)
    public ResponseDto<Boolean> checkUser(CheckUserDto checkUserDto, User user) {
        return ResponseDto.success(passwordEncoder.matches(checkUserDto.getPassword(), user.getPassword()));
    }


    //    @Transactional
//    //code -> 인가코드. 카카오에서 Param으로 넘겨준다.
//    public ResponseEntity<ResponseDto<LoginResponseDto>> kakaoLogin(String code, String urlType) throws IOException {
//        // 1. "인가 코드"로 "액세스 토큰" 요청
//        String accessToken = getToken(code, urlType);
//
//        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
//        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
//
//        // 3. 필요시에 회원가입
//        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
//
//        // 4. JWT 토큰 반환
//        HttpHeaders responseHeader = new HttpHeaders();
//        String createToken = jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());
//        responseHeader.add(JwtUtil.AUTHORIZATION_HEADER, createToken);
//
//        Boolean checkUser = kakaoUser.getDepartment() != null && kakaoUser.getEmpName() != null && kakaoUser.getPhone() != null;
//
//        return ResponseEntity.ok()
//                .headers(responseHeader)
//                .body(ResponseDto.success(LoginResponseDto.of(kakaoUser, checkUser)));
//    }
//
//    //     1. "인가 코드"로 "액세스 토큰" 요청
//    private String getToken(String code, String urlType) throws JsonProcessingException {
//        String redirectUrl = urlType.equals("local") ? redirectLocalUrl : redirectServerUrl;
//
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", apiKey); // 본인의 발급받은 API 키 넣기
//        body.add("redirect_uri", redirectUrl);
//        body.add("code", code);
//
////        https://bipum-in.shop/api/user/kakao/callback
////        http://localhost:8080/api/user/kakao/callback
////        http://localhost:3000/api/user/kakao/callback
//
//        // HTTP 요청 보내기1
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
//                new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper(); // 받은 것을 Json형태로 파싱
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//
//        return jsonNode.get("access_token").asText();
//    }
//
//    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
//    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoUserInfoRequest,
//                String.class
//        );
//
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        Long id = jsonNode.get("id").asLong();
//        String username = jsonNode.get("kakao_account") //이메일은 username으로 사용
//                .get("email").asText();
//        String profileImage = jsonNode.get("properties")
//                .get("profile_image").asText();
//
//        log.info("카카오 사용자 정보: " + id + ", " + username + ", " + profileImage);
//
//        return KakaoUserInfoDto.builder()
//                .id(id).username(username).image(profileImage).build();
//    }
//
//
//    // 3. 필요시에 회원가입
//    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {//받은 사용자 정보를 DTO로 받아옴
//        // DB 에 중복된 Kakao Id 가 있는지 확인
//        User kakaoUser = userRepository.findByKakaoId(kakaoUserInfo.getId()).orElse(null);
//
//        if (kakaoUser == null) { // 유저가 없으면 새로 회원가입..
//            // password: random UUID
//            String password = UUID.randomUUID().toString();
//            String encodedPassword = passwordEncoder.encode(password);
//
//            //Username이 이메일이므로 주의
//            kakaoUser = User.builder().
//                    kakaoId(kakaoUserInfo.getId()).
//                    password(encodedPassword).
//                    username(kakaoUserInfo.getUsername()).
//                    image(kakaoUserInfo.getImage()).
//                    role(UserRoleEnum.USER).
//                    alarm(true).
//                    deleted(false).
//                    build();
//
//            userRepository.save(kakaoUser);
//        }
//
//        return kakaoUser;
//    }

    //    public String encryptUser(LoginResponseDto loginResponseDto)
//            throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
//        Cipher cipher = Cipher.getInstance(alg);
//        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
//
//        byte[] encrypted = cipher.doFinal(convertToBytes(loginResponseDto));
//        String base64Encrpyted = Base64.getEncoder().encodeToString(encrypted);
//
//        // Generate HMAC
//        String hmac = generateHmac(encrypted, key);
//
//        return base64Encrpyted + "." + hmac;
//    }
//
//    public static byte[] convertToBytes(Serializable loginResponseDto) throws IOException{
//        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
//        ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
//
//        objectOutStream.writeObject(loginResponseDto);
//        objectOutStream.flush();
//        objectOutStream.close();
//
//        return byteOutStream.toByteArray();
//    }
//
//    public String generateHmac(byte[] data, String key)
//            throws NoSuchAlgorithmException, InvalidKeyException {
//        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
//        Mac mac = Mac.getInstance("HmacSHA256");
//        mac.init(signingKey);
//        byte[] hmacBytes = mac.doFinal(data);
//        return Base64.getEncoder().encodeToString(hmacBytes);
//    }

//    public AccessTokenDto refreshToken(User user) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("client_id", clientId); // 클라이언트 Id
//        body.add("client_secret", clientSecret); // 클라이언트 Secret
//        body.add("refresh_token", user.getRefreshToken()); // Refresh Token
//        body.add("grant_type", "refresh_token");
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> googleTokenRequest =
//                new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://oauth2.googleapis.com/token",
//                HttpMethod.POST,
//                googleTokenRequest,
//                String.class
//        );
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.readValue(response.getBody(), AccessTokenDto.class);
//    }

    public void unlinkGoogleAPI(User user, String bearerToken) {
        //HTTP 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleUserDeleteRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://accounts.google.com/o/oauth2/revoke?token=" + user.getAccessToken(),
                HttpMethod.GET,
                googleUserDeleteRequest,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(ErrorCode.FailedRevokeGoogleAccessToken);
        }
    }

    @Transactional
    public ResponseDto<String> changePassword(ChangePasswordDto changePasswordDto, User user) {
        User foundUser = getUser(user.getId());
        foundUser.changePassword(passwordEncoder.encode(changePasswordDto.getPassword()));
        return ResponseDto.success("비밀번호 수정 완료.");
    }

    private void deleteRefreshToken(String username) {
        Optional<RefreshToken> redisEntity = redisRepository.findById(username);
        if (redisEntity.isPresent()) {
            redisRepository.deleteById(username);
        }
    }
}