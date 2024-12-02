package com.qt.VideoPlatformAPI.Config;

import com.qt.VideoPlatformAPI.Auth.JWTAuthenticationFilter;
import com.qt.VideoPlatformAPI.User.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS
                .authorizeHttpRequests(req -> req
                        // auth
                        .requestMatchers("**/auth/**").permitAll()
                        .requestMatchers("**/users/checkUsernameAvailability").permitAll()
                        .requestMatchers("**/users/checkEmailAvailability").permitAll()

                        // user
                        .requestMatchers(HttpMethod.GET, "**/users/{username}").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/users/{username}/follow").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/users/{username}/unfollow").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/users/followings").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/users/profilePic").authenticated()

                        // video
                        .requestMatchers(HttpMethod.POST, "**/videos/new/").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/file/video/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/videos/{videoId}/thumbnail").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/videos/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "**/videos/user/{userId}").permitAll()

                        // video like
                        .requestMatchers(HttpMethod.POST, "**/videos/likes/{videoId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "**/videos/likes/{videoId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/videos/likes/{videoId}").authenticated()

                        // comment
                        .requestMatchers(HttpMethod.POST, "**/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "**/comments").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "**/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/comments/video/{videoId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "**/comments/like/{commentId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "**/comments/like/{commentId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/comments/like/{commentId}").authenticated()

                        // playlist
                        .requestMatchers(HttpMethod.POST, "**/playlists/").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/playlists/{playlistId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "**/playlists/user/{userId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "**/playlists/{playlistId}/add").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "**/playlists/{playlistId}/remove").authenticated()
                        .requestMatchers(HttpMethod.POST, "**/playlists/{playlistId}/thumbnail").authenticated()

                        // serve video manifest file
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Cho phép tất cả các domain (có thể thay bằng domain cụ thể nếu cần)
        config.addAllowedHeader("*"); // Cho phép tất cả các headers
        config.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP
        config.setMaxAge(3600L); // Cache CORS trong 1 giờ
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng CORS cho tất cả endpoints
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
