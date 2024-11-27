package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.AvailabilityResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final IUserConnectionRepository userConnectionRepository;

    @Override
    public UserProfile loadUserByUsername(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username can not be null or empty");
        }

        try {
            Optional<UserProfile> optionalUserProfile = userRepository.findByUsername(username);

            if(optionalUserProfile.isEmpty()) {
                throw new UsernameNotFoundException("The username does not exist");
            }
            return optionalUserProfile.get();

        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user", e);
        }
    }

    public ResponseEntity<AvailabilityResponse> checkUsernameAvailability(String username) {
         Boolean isExisted = userRepository.existByUsername(username);
         if(isExisted)
             return ResponseEntity.ok(new AvailabilityResponse(Boolean.TRUE, "username " + username + " exists", HttpStatus.OK,Boolean.TRUE));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvailabilityResponse(Boolean.FALSE, "username " + username + " does not exist", HttpStatus.NOT_FOUND, Boolean.FALSE));
    }

    public ResponseEntity<AvailabilityResponse> checkEmailAvailability(String email) {
        Boolean isExisted = userRepository.existByEmail(email);
        if(isExisted)
            return ResponseEntity.ok(new AvailabilityResponse(Boolean.TRUE, "email " + email + " exists", HttpStatus.OK,Boolean.TRUE));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvailabilityResponse(Boolean.FALSE, "email " + email + " does not exist", HttpStatus.NOT_FOUND,Boolean.FALSE));
    }

    public APIResponse followAUser(String username) {
        UserProfile following = loadUserByUsername(username);
        UserProfile follower = getCurrentUser();
        // check if this connection exists or not
        if(userConnectionRepository.existsByFollowerAndFollowing(follower, following)) {
            return new APIResponse(Boolean.FALSE, "You already followed " + following.getUsername(), HttpStatus.BAD_REQUEST);
        }
        UserConnection userConnection = new UserConnection();
        userConnection.setFollower(follower);
        userConnection.setFollowing(following);

        try {
            userConnectionRepository.save(userConnection);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return new APIResponse(Boolean.TRUE, "Follow " + following.getUsername() + " successfully", HttpStatus.OK);
    }
    public APIResponse unfollowAUser(String username) {
        UserProfile following = loadUserByUsername(username);
        UserProfile follower = getCurrentUser();
        // check if this connection exists or not
        if(!userConnectionRepository.existsByFollowerAndFollowing(follower, following)) {
            return new APIResponse(Boolean.FALSE, "You did not follow " + following.getUsername() + " yet", HttpStatus.BAD_REQUEST);
        }
        Optional<UserConnection> userConnectionOptional =
        userConnectionRepository.findByFollowerAndFollowing(follower, following);

        userConnectionRepository.delete(userConnectionOptional.get());

        return new APIResponse(Boolean.TRUE, "You unfollow " + following.getUsername() + " successfully", HttpStatus.OK);
    }

    public Set<String> getAllFollowings() {
        UserProfile userProfile = getCurrentUser();
        Set<UserConnection> followings = userProfile.getFollowing();
        Set<String> usernameList = new HashSet<>();

        followings.forEach(userConnection -> {
            usernameList.add(userConnection.getFollowing().getUsername());
        });
        return usernameList;
    }

    public UserProfile getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) {
            throw new AuthenticationServiceException("Not authenticated");
        }
        return (UserProfile) auth.getPrincipal();
    }

    @Transactional
    public UserProfile updateUser(UserProfile newUserInfo) {
        UserProfile currentUser = getCurrentUser();

        currentUser.setUsername(newUserInfo.getUsername());
        currentUser.setFullName(newUserInfo.getFullName());
        //currentUser.setEmail(newUserInfo.getFullName());
        currentUser.setBio(newUserInfo.getBio());
        currentUser.setProfilePic(newUserInfo.getProfilePic());
        currentUser.setDateOfBirth(newUserInfo.getDateOfBirth());
        currentUser.setPhone(newUserInfo.getPhone());

        return userRepository.save(currentUser);
    }
}
