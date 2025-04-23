package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Event.NotificationProducer;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class CommentService {
    private final Logger logger;
    private final ICommentRepository iCommentRepository;
    private final ICommentLikeRepository iCommentLikeRepository;
    private final VideoService videoService;
    private final UserService userService;
    private final CustomCommentRepository customCommentRepository;
    private final NotificationProducer notificationProducer;

    public Comment addAComment(Comment comment) {
        // check if video exist
        if(!videoService.isVideoExistent(comment.getVideoId()))
            throw new IllegalArgumentException("Video with that id does not exist");

        //check if video comment on or off
        Video video = videoService.getVideoById(comment.getVideoId());
        Boolean isCommentOff = video.getIsCommentOff();
        if(isCommentOff == null || isCommentOff)
            throw new IllegalArgumentException("Video with that id has comment off");

        if(comment.getContent() == null || comment.getContent().isEmpty())
            throw new IllegalArgumentException("Comment content is null or empty");

        UserProfile currentUser = userService.getCurrentUser();
        comment.setUserId(currentUser.getId());
        comment.setUsername(currentUser.getUsername());
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setReplies(new ArrayList<>());
        comment.setIsEdited(Boolean.FALSE);

        // check if replyTo exist
        if(comment.getReplyTo() != null) {
            Comment parentComment = getCommentById(comment.getReplyTo());

            videoService.increaseCommentCount(comment.getVideoId());
            Comment savedComment = iCommentRepository.save(comment);
            savedComment.setVideoId(parentComment.getVideoId()); // set the video id for the child comment

            // replyCount + 1
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            // add child to list
            parentComment.getReplies().add(new ObjectId(savedComment.getId()));
            iCommentRepository.save(parentComment);
            // send event
            notificationProducer.commentEvent(comment, video, parentComment);

            return savedComment;
        }

        videoService.increaseCommentCount(comment.getVideoId());
        // send event
        notificationProducer.commentEvent(comment, video, null);
        return iCommentRepository.save(comment);
    }

    public Comment getCommentById(String commentId) {
        Optional<Comment> commentOptional = iCommentRepository.findById(commentId);
        if(commentOptional.isEmpty())
            throw new IllegalArgumentException("Comment with id: " + commentId + " does not exist");
        return commentOptional.get();
    }

    public List<Comment> getAllCommentOfCurrentUser() {
        UserProfile userProfile = userService.getCurrentUser();
        return iCommentRepository.findAllByUserId(userProfile.getId());
    }

    public void deleteComment(String commentId) {
        Comment comment = getCommentById(commentId);
        Long userId = userService.getCurrentUser().getId();
        // check the user - who delete the comment, owner or video owner,
        // they both have the right to delete this comment
        if(!Objects.equals(userId, comment.getUserId()) &&
          !Objects.equals(userId, videoService.getVideoById(comment.getVideoId()).getUserId())) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        // delete all the child comment(recursion async)
        CompletableFuture.runAsync(() -> {
            List<ObjectId> childrenComment = comment.getReplies();
            for(ObjectId child : childrenComment) {
                iCommentRepository.deleteById(child.toHexString());
            }
        }).thenAccept((res) -> {
            logger.info("Deleted all children comment");
        });

        if(comment.getReplyTo() != null) {
            decreaseReplyCount(comment.getReplyTo());
            Comment parent = getCommentById(comment.getReplyTo());
            parent.getReplies().remove(commentId); // remove the child from replies of parent
        }

        videoService.decreaseCommentCount(getCommentById(commentId).getVideoId());
        iCommentRepository.deleteById(commentId);
    }

    public Comment updateComment(Comment comment) {
        Comment updatedComment = getCommentById(comment.getId());
        Long currentUserId = userService.getCurrentUser().getId();
        Long commentOwnerId = updatedComment.getUserId();

        if(!Objects.equals(currentUserId, commentOwnerId))
            throw new AccessDeniedException("You are not authorized to update this comment");

        updatedComment.setIsEdited(Boolean.TRUE);
        updatedComment.setContent(comment.getContent());

        return iCommentRepository.save(updatedComment);
    }

    public List<Comment> getAllParentCommentByVideoId(String videoId, Integer page, Integer size, boolean ascending) {
        List<Comment> commentList = customCommentRepository.getAllParentComment(videoId, page, size);

        if(!ascending) commentList.sort(Comparator.comparing(TimeAudit::getCreatedAt));
        else commentList.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return commentList;
    }

    // remove
    public List<Comment> getAllChildrenCommentByTimestamp(String commentId, boolean ascending) {
        List<Comment> commentChildList;
        if(ascending)
            commentChildList = iCommentRepository.findAllByReplyToOrderByCreatedAtAsc(commentId);
        else
            commentChildList = iCommentRepository.findAllByReplyToOrderByCreatedAtDesc(commentId);
        return commentChildList;
    }

    public List<Comment> getAllChildrenComment(String commentId, Integer page, Integer size, boolean ascending) {
        List<Comment> commentList = customCommentRepository.getAllChildrenComment(commentId, page, size);

        if(!ascending) commentList.sort(Comparator.comparing(TimeAudit::getCreatedAt));
        else commentList.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return commentList;
    }

    public void deleteAllCommentsByVideoId(String videoId) {
        List<Comment> commentList = iCommentRepository.findAllByVideoIdOrderByCreatedAtAsc(videoId);
        for(Comment c : commentList) {
            iCommentRepository.deleteById(c.getId());
        }
    }

    // Manage comments for current user
    public List<Comment> getAllCommentFromMyVideos(String videoId, Integer page, Integer size) {
        UserProfile user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Comment> pageComments = iCommentRepository.findAllCommentForAllVideosByUserId(user.getId(), pageable);

        // temporary solution
        List<Comment> comments = pageComments.getContent();
        if(videoId != null) return videoIdFilter(comments, videoId);
        return comments;
    }

    public List<Comment> getAllCommentFromMyVideosThatCurrentUserReplied(String videoId) {
        UserProfile user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(0, 999999, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Comment> pageComments = iCommentRepository.findAllCommentForAllVideosByUserId(user.getId(), pageable);

        List<Comment> comments = pageComments.getContent();
        List<Comment> result = new ArrayList<Comment>();
        for(Comment c : comments) {
            List<ObjectId> replies = c.getReplies();
            for(ObjectId r : replies) {
                if(checkReplied(r.toHexString(), user.getId())) {
                    result.add(c);
                    break;
                }
            }
        }
        // temporary solution
        if(videoId != null) return videoIdFilter(result, videoId);
        return result;
    }

    public List<Comment> getAllCommentFromMyVideosThatCurrentUserNotReplied(String videoId) {
        UserProfile user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(0, 999999, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Comment> pageComments = iCommentRepository.findAllCommentForAllVideosByUserId(user.getId(), pageable);

        List<Comment> comments = new ArrayList<>(pageComments.getContent());
        List<Comment> toRemove = new ArrayList<>();
        for(Comment c : comments) {
            List<ObjectId> replies = c.getReplies();
            for(ObjectId r : replies) {
                if(checkReplied(r.toHexString(), user.getId())) {
                    toRemove.add(c);
                    break;
                }
            }
        }
        if(!toRemove.isEmpty()) comments.removeAll(toRemove);
        // temporary solution
        if(videoId != null) return videoIdFilter(comments, videoId);
        return comments;
    }

    // It's just a temporary solution I do in the backend for the 3 above methods,
    // I will refactor the Mongo database to get better performance of filter feature
    List<Comment> videoIdFilter(List<Comment> comments, String videoId) {
        videoService.getVideoById(videoId);
        List<Comment> theFuckingRightComments = new ArrayList<>();
        for(Comment c : comments) {
            if(Objects.equals(c.getVideoId(), videoId))
                theFuckingRightComments.add(c);
        }
        return theFuckingRightComments;
    }

    public void decreaseReplyCount(String commentId) {
        Comment comment = getCommentById(commentId);
        comment.setReplyCount(comment.getReplyCount() - 1);
        iCommentRepository.save(comment);
    }

    public CommentLike likeAComment(String commentId) {
//        if(checkLikeComment(commentId))
//            throw new IllegalArgumentException("You've already liked this comment");
        UserProfile user = userService.getCurrentUser();
        Optional<CommentLike> commentLikeOptional = iCommentLikeRepository.findByCommentIdAndUserId(new
                ObjectId(commentId), user.getId());
        if(commentLikeOptional.isPresent())
            throw new IllegalArgumentException("You've already liked this comment");

        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userService.getCurrentUser().getId());

        //increaseCommentLikeCount(commentId);
        Comment comment = getCommentById(commentId);
        comment.setLikeCount(comment.getLikeCount() + 1);
        iCommentRepository.save(comment);

        // send notification
        if(!user.getUsername().equals(comment.getUsername()))
            notificationProducer.likeCommentEvent(user.getUsername(),
                    comment, videoService.getVideoById(comment.getVideoId()));

        return iCommentLikeRepository.save(commentLike);
    }

    public void removeALikeComment(String commentId) {
        Optional<CommentLike> commentLike = iCommentLikeRepository.findByCommentIdAndUserId(new ObjectId(commentId),
                userService.getCurrentUser().getId());
        if(commentLike.isEmpty())
            throw new IllegalArgumentException("You've not liked this comment before");

        iCommentLikeRepository.delete(commentLike.get());
        decreaseCommentLikeCount(commentId);
    }

    public void decreaseCommentLikeCount(String commentId) {
        Comment comment = getCommentById(commentId);
        comment.setLikeCount(comment.getLikeCount() - 1);
        iCommentRepository.save(comment);
    }

    public boolean checkLikeComment(String commentId) {
        Optional<CommentLike> commentLike = iCommentLikeRepository.findByCommentIdAndUserId(new ObjectId(commentId),
                userService.getCurrentUser().getId());
        return commentLike.isPresent();
    }

    public boolean checkReplied(String commentId, Long userId) {
        Comment c = getCommentById(commentId);
        return Objects.equals(c.getUserId(), userId);
    }
}
