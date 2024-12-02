package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping()
    public ResponseEntity<APIResponseWithData<Comment>> addAComment(@Valid @RequestBody Comment comment) {
        if(comment == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment is null or empty", HttpStatus.BAD_REQUEST, null)
            );

        return ResponseEntity.ok(new APIResponseWithData<Comment>(Boolean.TRUE, "Added a comment successfully", HttpStatus.OK, commentService.addAComment(comment)));
    }
    @DeleteMapping()
    public ResponseEntity<APIResponse> deleteAComment(@RequestParam String commentId) {
        if(commentId == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment id is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Deleted a comment successfully", HttpStatus.OK));
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllCommentByVideoIdByTimestampOrder(@PathVariable String videoId,
        @RequestParam(defaultValue = "newest") String order) {
        if(videoId == null || videoId.isBlank())
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<>(Boolean.FALSE, "Video id is null or blank", HttpStatus.BAD_REQUEST, null));
        List<Comment> commentList = commentService.getAllCommentByVideoIdByTimestamp(videoId, "oldest".equalsIgnoreCase(order));
        return ResponseEntity.ok(new APIResponseWithData<List<Comment>>(Boolean.TRUE, "Get all comment in a video successfully", HttpStatus.OK,
        commentList));
    }

    @PatchMapping()
    public ResponseEntity<APIResponseWithData<Comment>> updateAComment(@RequestBody Comment comment) {
        if(comment == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        return ResponseEntity.ok(new APIResponseWithData<Comment>(Boolean.TRUE, "Updated a comment successfully", HttpStatus.OK, commentService.updateComment(comment)));
    }

    @PostMapping("/like/{commentId}")
    public ResponseEntity<APIResponseWithData<CommentLike>> likeAComment(@PathVariable String commentId) {
        if(commentId == null || commentId.isBlank())
            return ResponseEntity.status(400).body(
                    new APIResponseWithData<CommentLike>(Boolean.FALSE, "Comment id is null or blank",
                    HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<CommentLike>(Boolean.TRUE, "Like comment successfully",
                HttpStatus.OK, commentService.likeAComment(commentId)));
    }

    @DeleteMapping("/like/{commentId}")
    public ResponseEntity<APIResponse> removeALikeComment(@PathVariable String commentId) {
        if(commentId == null || commentId.isBlank())
            return ResponseEntity.status(400).body(
                    new APIResponse(Boolean.FALSE, "Comment id is null or blank",
                            HttpStatus.BAD_REQUEST));

        commentService.removeALikeComment(commentId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Unlike comment successfully",
                HttpStatus.OK));
    }

    @GetMapping("/like/{commentId}")
    public ResponseEntity<APIResponse> checkLikeComment(@PathVariable String commentId) {
        if(commentId == null || commentId.isBlank())
            return ResponseEntity.status(400).body(
                    new APIResponse(Boolean.FALSE, "Comment id is null or blank",
                            HttpStatus.BAD_REQUEST));
        if(commentService.checkLikeComment(commentId))
            return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Liked", HttpStatus.OK));
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Not liked yet", HttpStatus.OK));
    }
}
