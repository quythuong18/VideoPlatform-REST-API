package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CommentService {
    private final ICommentRepository iCommentRepository;
    private final VideoService videoService;
    private final UserService userService;

    public Comment addAComment(Comment comment) {
        // check if replyTo exist
        if(comment.getReplyTo() != null && isCommentExistent(comment.getReplyTo()))
            throw new IllegalArgumentException("Video with that id does not exist");

        // check if video exist
        if(!videoService.isVideoExistent(comment.getVideoId()))
            throw new IllegalArgumentException("Video with that id does not exist");

        //check if video comment on or off
        if(videoService.getVideoById(comment.getVideoId()).getIsCommentOff())
            throw new IllegalArgumentException("Video with that id has comment off");

        if(comment.getContent() == null || comment.getContent().isEmpty())
            throw new IllegalArgumentException("Comment content is null or empty");
        comment.setUserId(userService.getCurrentUser().getId());
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setIsEdited(Boolean.FALSE);

        videoService.increaseCommentCount(comment.getVideoId());
        return iCommentRepository.save(comment);
    }

    public Comment getCommentById(String commentId) {
        Optional<Comment> commentOptional = iCommentRepository.findById(commentId);
        if(commentOptional.isEmpty())
            throw new IllegalArgumentException("Comment with id: " + commentId + "does not exist");
        return commentOptional.get();
    }

    public void deleteComment(String commentId) {
        // check if comment exist
        if(!isCommentExistent(commentId))
            throw new IllegalArgumentException("Comment with that id does not exist");

        videoService.decreaseCommentCount(getCommentById(commentId).getVideoId());
        iCommentRepository.deleteById(commentId);
    }

    public Comment updateComment(Comment comment) {
        // check if comment exist
        if(!isCommentExistent(comment.getId()))
            throw new IllegalArgumentException("Comment with that id does not exist");

        Comment updatedComment = getCommentById(comment.getId());
        updatedComment.setIsEdited(Boolean.TRUE);
        updatedComment.setContent(comment.getContent());

        return iCommentRepository.save(updatedComment);
    }

    public boolean isCommentExistent(String commentId) {
        return iCommentRepository.existsById(commentId);
    }

    public List<Comment> getAllCommentByVideoId(String videoId) {
        List<Comment> commentList = iCommentRepository.findAllByVideoId(videoId);
        return commentList;
    }
}
