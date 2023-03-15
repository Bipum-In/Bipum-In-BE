package com.sparta.bipuminbe.user.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.jwt.JwtUtil;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
//
//    @Transactional
//    public ResponseDto<String> signup(SignupRequestDto signupRequestDto) {
//        String username = signupRequestDto.getUsername();
//        String password = passwordEncoder.encode(signupRequestDto.getPassword());
//
//        Optional<User> foundUsername = userRepository.findByUsername(signupRequestDto.getUsername());
//
//        // 예외처리 커스텀 할것
//        if(foundUsername.isPresent()){
//            throw new CustomException(ErrorCode.DuplicateUsername);
//        }
//
//        Optional<User> foundNickname = userRepository.findByNickname(signupRequestDto.getNickname());
//        if(foundNickname.isPresent()){
//            throw new CustomException(ErrorCode.DuplicatedNickname);
//        }
//
//        UserRoleEnum role = UserRoleEnum.USER;
//        if(signupRequestDto.isAdmin()){
//            if(!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)){
//                // 예외처리 커스텀 할것
//                throw new CustomException(ErrorCode.NotMatchAdminPassword);
//            }
//            role = UserRoleEnum.ADMIN;
//        }
//
//        userRepository.save(new User(username, password, signupRequestDto.getEmail(), signupRequestDto.getNickname(), role));
//
//        return ResponseDto.success("회원가입 성공");
//    }

}
