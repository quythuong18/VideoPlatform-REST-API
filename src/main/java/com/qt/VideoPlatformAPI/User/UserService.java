package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.DTO.UserInfoDTO;
import com.qt.VideoPlatformAPI.DTO.UserPublicDTO;
import com.qt.VideoPlatformAPI.Event.NotificationProducer;
import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final IUserRepository userRepository;
    private final IUserConnectionRepository userConnectionRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper mapper;
    private final NotificationProducer notificationProducer;

    public UserPublicDTO getAPublicUserByUsername(String username) {
        Optional<UserProfile> userProfileOptional = userRepository.findByUsername(username);
        if(userProfileOptional.isEmpty())
            throw new IllegalArgumentException("Username does not exist");
        return mapper.map(userProfileOptional.get(), UserPublicDTO.class);
    }

    public UserPublicDTO getAPublicUserById(Long userId) {
        Optional<UserProfile> userProfileOptional = userRepository.findById(userId);
        if(userProfileOptional.isEmpty())
            throw new IllegalArgumentException("Username does not exist");
        return mapper.map(userProfileOptional.get(), UserPublicDTO.class);
    }

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

    public UserProfile getUserByUserId(Long userId) {
        Optional<UserProfile> userProfileOptional = userRepository.findById(userId);
        if(userProfileOptional.isEmpty())
            throw new IllegalArgumentException("UserId does not exist");
        return userProfileOptional.get();

    }

    public Boolean checkUsernameAvailability(String username) {
         return userRepository.existByUsername(username);
    }

    public Boolean checkEmailAvailability(String email) {
        return userRepository.existByEmail(email);
    }

    public Boolean isPrivateUserByUsername(String username) {
        Optional<UserProfile> userProfileOptional = userRepository.findByUsername(username);
        if(userProfileOptional.isEmpty())
            throw new IllegalArgumentException("Username does not exist");
        return userProfileOptional.get().getIsPrivate();
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


        increaseFollowsCount(follower, following);
        userConnectionRepository.save(userConnection);
        // send notification
        notificationProducer.followingEvent(follower.getUsername(), following.getUsername());

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

        decreaseFollowsCount(follower, following);
        userConnectionRepository.delete(userConnectionOptional.get());

        return new APIResponse(Boolean.TRUE, "You unfollow " + following.getUsername() + " successfully", HttpStatus.OK);
    }

    public void increaseFollowsCount(UserProfile follower, UserProfile following) {
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        following.setFollowerCount(following.getFollowerCount() + 1);
        userRepository.save(follower);
        userRepository.save(following);
    }

    public void decreaseFollowsCount(UserProfile follower, UserProfile following) {
        follower.setFollowingCount(follower.getFollowingCount() - 1);
        following.setFollowerCount(following.getFollowerCount() - 1);
        userRepository.save(follower);
        userRepository.save(following);
    }

    public Set<UserInfoDTO> getAllMyFollowings(Integer page, Integer size) {
        UserProfile userProfile = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<UserConnection> userConnectionList = userConnectionRepository.findByFollower(userProfile, pageable);

        Set<UserInfoDTO> userDTOlist = new HashSet<>();

        userConnectionList.forEach(userConnection -> {
            LOGGER.info(userConnection.toString());
            String username = userConnection.getFollowing().getUsername();
            UserInfoDTO userInfoDTO = mapper.map(getAPublicUserByUsername(username), UserInfoDTO.class);
            userDTOlist.add(userInfoDTO);
        });
        return userDTOlist;
    }

    public Set<UserInfoDTO> getAllMyFollowers(Integer page, Integer size) {
        UserProfile userProfile = getCurrentUser();
        return getAllFollowersByUserProfile(userProfile, page, size);
    }

    public Set<String> getAllFollowersByUsername(String username, Integer page, Integer size) {
        Optional<UserProfile> userProfileOptional = userRepository.findByUsername(username);
        if(userProfileOptional.isEmpty()) throw new IllegalArgumentException("Username does not exist");
        Set<String> usernameList = new HashSet<>();
        Set<UserInfoDTO> userInfoDTOList =
                getAllFollowersByUserProfile(userProfileOptional.get(), page, size) ;
        for(UserInfoDTO uInfo : userInfoDTOList) {
            usernameList.add(uInfo.getUsername());
        }
        return usernameList;
    }

    public Set<UserInfoDTO> getAllFollowersByUserProfile(UserProfile user, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<UserConnection> userConnectionList = userConnectionRepository.findByFollowing(user, pageable);

        Set<UserInfoDTO> userInfoDTOList = new HashSet<>();

        userConnectionList.forEach(userConnection -> {
            LOGGER.info(userConnection.toString());
            String username = userConnection.getFollower().getUsername();

            UserInfoDTO userInfoDTO = mapper.map(getAPublicUserByUsername(username), UserInfoDTO.class);
            userInfoDTO.setIsFollowing(
                    userConnectionRepository.existsByFollowerUsernameAndFollowingUsername(
                            user.getUsername(), username
                    )
            );

            userInfoDTOList.add(userInfoDTO);
        });
        return userInfoDTOList;
    }

    public UserProfile getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) {
            throw new AuthenticationServiceException("Authentication null");
        }
        LOGGER.info(auth.getPrincipal().toString());
        if(auth.getPrincipal().toString().equals("anonymousUser")) return null;

        return (UserProfile) auth.getPrincipal();
    }

    // check if username follow the current user
    public Boolean checkFollower(String username) {
        UserProfile currentUser = getCurrentUser();
        UserProfile user = loadUserByUsername(username);
        return userConnectionRepository.existsByFollowerAndFollowing(user, currentUser);
    }

    // check if current user follow the username
    public Boolean checkFollowing(String username) {
        UserProfile currentUser = getCurrentUser();
        UserProfile user = loadUserByUsername(username);
        return userConnectionRepository.existsByFollowerAndFollowing(currentUser, user);
    }

    @Transactional
    public UserProfile updateUser(UserProfile newUserInfo) {
        UserProfile currentUser = getCurrentUser();

        //currentUser.setUsername(newUserInfo.getUsername());
        if(newUserInfo.getFullName() != null)
            currentUser.setFullName(newUserInfo.getFullName());
        //currentUser.setEmail(newUserInfo.getFullName());
        if(newUserInfo.getBio() != null)
            currentUser.setBio(newUserInfo.getBio());
        if(newUserInfo.getProfilePic() != null)
            currentUser.setProfilePic(newUserInfo.getProfilePic());
        if(newUserInfo.getIsPrivate() != null)
            currentUser.setIsPrivate(newUserInfo.getIsPrivate());
        if(newUserInfo.getDateOfBirth() != null)
            currentUser.setDateOfBirth(newUserInfo.getDateOfBirth());
        if(newUserInfo.getPhone() != null)
            currentUser.setPhone(newUserInfo.getPhone());

        return userRepository.save(currentUser);
    }

    @Transactional
    public String updateProfilePic(MultipartFile img) throws IOException {
        UserProfile user = getCurrentUser();
        String url;
        if(user.getProfilePic() == null || user.getProfilePic().isBlank())
            url = cloudinaryService.uploadPhoto(img, "profile", user.getId().toString());
        else
            url = cloudinaryService.updatePhoto(img, "profile", user.getId().toString());
        user.setProfilePic(url);
        userRepository.save(user);
        return url;
    }

    public String getProfilePicByUsername(String username) {
        Optional<String> userProfilePicOptional =
                userRepository.findUserProfilePicByUsername(username);
        if(userProfilePicOptional.isEmpty())
            throw new IllegalArgumentException("Username is not found or profile picture url is null");

        return userProfilePicOptional.get();
    }

    public List<UserInfoDTO> searchByUsername(String searchPattern) {
        List<UserProfile> userProfileList = userRepository.findByUsernameContaining(searchPattern);
        List<UserInfoDTO> userInfoDTOList = new ArrayList<>();


        for(UserProfile u : userProfileList) {
            userInfoDTOList.add(mapper.map(u, UserInfoDTO.class));
        }

        UserProfile currentUser = getCurrentUser();
        if(currentUser == null) return userInfoDTOList;

        for(UserInfoDTO uInfoDTO : userInfoDTOList) {
            uInfoDTO.setIsFollowing(userConnectionRepository.existsByFollowerUsernameAndFollowingUsername
                            (currentUser.getUsername(), uInfoDTO.getUsername()));
        }
        return userInfoDTOList;
    }
}
