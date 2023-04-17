package com.sparta.bipuminbe.common.config;

import com.sparta.bipuminbe.common.jwt.JwtAuthFilter;
import com.sparta.bipuminbe.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsUtils;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // resources 접근 허용 설정
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/api/user/login/google").permitAll()
                .antMatchers("/api/user/login/master").permitAll()
                .antMatchers("/api/user/logout").permitAll()
                .antMatchers("/api/user/login/toy").permitAll()
                // swagger
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
//                .antMatchers("**").permitAll()
                .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 이 설정을 해주지 않으면 밑의 corsConfigurationSource가 적용되지 않습니다!
        http.cors();

        return http.build();
    }

// 이 설정을 해주면, 우리가 설정한대로 CorsFilter가 Security의 filter에 추가되어
// 예비 요청에 대한 처리를 해주게 됩니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 사전에 약속된 출처를 명시
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://hanghae77.s3-website.ap-northeast-2.amazonaws.com");
        config.addAllowedOrigin("http://hanghae1teamwork.s3-website.ap-northeast-2.amazonaws.com/");
        config.addAllowedOrigin("https://bipum-in-fe-two.vercel.app/");
        config.addAllowedOrigin("https://www.bipumin.shop");

        // 특정 헤더를 클라이언트 측에서 사용할 수 있게 지정
        // 만약 지정하지 않는다면, Authorization 헤더 내의 토큰 값을 사용할 수 없음
        config.addExposedHeader("*");

        // 본 요청에 허용할 HTTP method(예비 요청에 대한 응답 헤더에 추가됨)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));

        // 본 요청에 허용할 HTTP header(예비 요청에 대한 응답 헤더에 추가됨)
        config.addAllowedHeader("*");

        // 기본적으로 브라우저에서 인증 관련 정보들을 요청 헤더에 담지 않음
        // 이 설정을 통해서 브라우저에서 인증 관련 정보들을 요청 헤더에 담을 수 있도록 해줍니다.
        config.setAllowCredentials(true);

        // allowCredentials 를 true로 하였을 때,
        // allowedOrigin의 값이 * (즉, 모두 허용)이 설정될 수 없도록 검증합니다.
        config.validateAllowCredentials();

        // 어떤 경로에 이 설정을 적용할 지 명시합니다. (여기서는 전체 경로)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
