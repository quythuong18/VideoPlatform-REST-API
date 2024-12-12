package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class CommentService {
    private static final Logger logger = Logger.getLogger(CommentService.class.getName());
    private final ICommentRepository iCommentRepository;
    private final ICommentLikeRepository iCommentLikeRepository;
    private final VideoService videoService;
    private final UserService userService;

    public Comment addAComment(Comment comment) {
        // check if video exist
        if(!videoService.isVideoExistent(comment.getVideoId()))
            throw new IllegalArgumentException("Video with that id does not exist");

        //check if video comment on or off
        Boolean isCommentOff = videoService.getVideoById(comment.getVideoId()).getIsCommentOff();
        if(isCommentOff == null || isCommentOff)
            throw new IllegalArgumentException("Video with that id has comment off");

        if(comment.getContent() == null || comment.getContent().isEmpty())
            throw new IllegalArgumentException("Comment content is null or empty");
        comment.setUserId(userService.getCurrentUser().getId());
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setReplies(new ArrayList<>());
        comment.setIsEdited(Boolean.FALSE);

        // check if replyTo exist
        if(comment.getReplyTo() != null) {
            if(isCommentExistent(comment.getReplyTo()))
                throw new IllegalArgumentException("Comment with that id does not exist");
            Comment parentComment = getCommentById(comment.getReplyTo());

            videoService.increaseCommentCount(comment.getVideoId());
            Comment savedComment = iCommentRepository.save(comment);
            savedComment.setVideoId(parentComment.getVideoId()); // set the video id for the child comment

            // replyCount + 1
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            // add child to list
            parentComment.getReplies().add(savedComment.getId());
            iCommentRepository.save(parentComment);
            return savedComment;
        }

        videoService.increaseCommentCount(comment.getVideoId());
        return iCommentRepository.save(comment);
    }

    public Comment getCommentById(String commentId) {
        Optional<Comment> commentOptional = iCommentRepository.findById(commentId);
        if(commentOptional.isEmpty())
            throw new IllegalArgumentException("Comment with id: " + commentId + "does not exist");
        return commentOptional.get();
    }

    public List<Comment> getAllCommentOfCurrentUser() {
        UserProfile userProfile = userService.getCurrentUser();
        return iCommentRepository.findAllByUserId(userProfile.getId());
    }

    public void deleteComment(String commentId) {
        // check if comment exist
        if(isCommentExistent(commentId))
            throw new IllegalArgumentException("Comment with that id does not exist");

        Comment comment = getCommentById(commentId);
        Long userId = userService.getCurrentUser().getId();
        // check the user - who delete the comment, owner or video owner,
        // they both have the right to delete this comment
        if(userId != comment.getUserId() && userId != videoService.getVideoById(comment.getVideoId()).getUserId()) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        // delete all the child comment(recursion async)
        CompletableFuture.runAsync(() -> {
            List<String> childrenComment = comment.getReplies();
            for(String child : childrenComment) {
                iCommentRepository.deleteById(child);
            }
        }).thenAccept((res) -> {
            logger.info("Deleted all children comment");
        });

        if(comment.getReplyTo() != null) {
            decreaseReplyCount(comment.getReplyTo());
        }

        videoService.decreaseCommentCount(getCommentById(commentId).getVideoId());
        iCommentRepository.deleteById(commentId);
    }

    public Comment updateComment(Comment comment) {
        // check if comment exist
        if(!isCommentExistent(comment.getId()))
            throw new IllegalArgumentException("Comment with that id does not exist");

        Comment updatedComment = getCommentById(comment.getId());

        if(userService.getCurrentUser().getId() != updatedComment.getUserId())
            throw new AccessDeniedException("You are not authorized to update this comment");

        updatedComment.setIsEdited(Boolean.TRUE);
        updatedComment.setContent(comment.getContent());

        return iCommentRepository.save(updatedComment);
    }

    public boolean isCommentExistent(String commentId) {
        return !iCommentRepository.existsById(commentId);
    }

    public List<Comment> getAllCommentByVideoIdByTimestamp(String videoId, boolean acesding) {
        List<Comment> commentList;
        if(acesding)
            commentList = iCommentRepository.findAllByVideoIdOrderByCreatedAtAsc(videoId);
        else
            commentList = iCommentRepository.findAllByVideoIdOrderByCreatedAtDesc(videoId);
        return commentList;
    }

    public List<Comment> getAllParentCommentByVideoIdByTimestamp(String videoId, boolean acesding) {
        List<Comment> commentList;
        if(acesding)
            commentList = iCommentRepository.findAllByVideoIdOrderByCreatedAtAsc(videoId);
        else
            commentList = iCommentRepository.findAllByVideoIdOrderByCreatedAtDesc(videoId);

        List<Comment> returnList = new ArrayList<>();
        for(Comment c : commentList) {
            if(c.getReplyTo() == null) {
                returnList.add(c);
            }
        }
        return returnList;
    }

    public List<Comment> getAllChildrenComment(String commentId) {
        List<Comment> commentChildList;
        commentChildList = iCommentRepository.findAllByReplyTo(commentId);
        return commentChildList;
    }

    public void decreaseReplyCount(String commentId) {
        Comment comment = getCommentById(commentId);
        comment.setReplyCount(comment.getReplyCount() - 1);
        iCommentRepository.save(comment);
    }

    // like comment
    public CommentLike likeAComment(String commentId) {
        if(checkLikeComment(commentId))
            throw new IllegalArgumentException("You've already liked this comment");
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userService.getCurrentUser().getId());

        increaseCommentLikeCount(commentId);
        return iCommentLikeRepository.save(commentLike);
    }

    public void removeALikeComment(String commentId) {
        Optional<CommentLike> commentLike = iCommentLikeRepository.findByCommentIdAndUserId(commentId,
                userService.getCurrentUser().getId());
        if(commentLike.isEmpty())
            throw new IllegalArgumentException("You've not liked this comment before");

        iCommentLikeRepository.delete(commentLike.get());
        decreaseCommentLikeCount(commentId);
    }

    public void increaseCommentLikeCount(String commentId) {
        Comment comment = getCommentById(commentId);
        comment.setLikeCount(comment.getLikeCount() + 1);
        iCommentRepository.save(comment);
    }
    public void decreaseCommentLikeCount(String commentId) {
        Comment comment = getCommentById(commentId);
        comment.setLikeCount(comment.getLikeCount() - 1);
        iCommentRepository.save(comment);
    }
    public boolean checkLikeComment(String commentId) {
        Optional<CommentLike> commentLike = iCommentLikeRepository.findByCommentIdAndUserId(commentId,
                userService.getCurrentUser().getId());
        return commentLike.isPresent();
    }
}
