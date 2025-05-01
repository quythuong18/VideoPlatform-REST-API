package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/")
    public ResponseEntity<APIResponseWithData<Comment>> addAComment(@RequestBody Comment comment) {
        if(comment == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment is null or empty", HttpStatus.BAD_REQUEST, null)
            );

        //return "Added a comment successfully";
        return ResponseEntity.ok(new APIResponseWithData<Comment>(Boolean.TRUE, "Add a comment in a video successfully", HttpStatus.OK,
                commentService.addAComment(comment)));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<APIResponseWithData<Comment>>  getACommentById(@PathVariable String commentId) {
        if(commentId == null || commentId.isBlank())
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment id is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        return ResponseEntity.ok(new APIResponseWithData(Boolean.TRUE, "Get comment by id successfully", HttpStatus.OK,
                commentService.getCommentById(commentId)));

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<APIResponse> deleteAComment(@PathVariable String commentId) {
        if(commentId == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment id is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Deleted a comment successfully", HttpStatus.OK));
    }

    @GetMapping("/video/parent/{videoId}")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllParentCommentByVideoId(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "newest") String order
    ) {
        if(videoId == null || videoId.isBlank())
            throw new IllegalArgumentException("Video id is null or blank");
        List<Comment> commentList = commentService.getAllParentCommentByVideoId(videoId, page, size, "oldest".equalsIgnoreCase(order));
        return ResponseEntity.ok(new APIResponseWithData<List<Comment>>(Boolean.TRUE, "Get all parent comment in a video successfully", HttpStatus.OK,
                commentList));
    }

    @GetMapping("/video/parent/authenticated/{videoId}")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllParentCommentByVideoIdAuthenticated(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "newest") String order
    ) {
        if(videoId == null || videoId.isBlank())
            throw new IllegalArgumentException("Video id is null or blank");
        List<Comment> commentList = commentService.getAllParentCommentByVideoIdAuthenticated(videoId, page, size, "oldest".equalsIgnoreCase(order));
        return ResponseEntity.ok(new APIResponseWithData<List<Comment>>(Boolean.TRUE, "Get all parent comment in a video successfully", HttpStatus.OK,
                commentList));
    }

    @GetMapping("/{commentId}/children")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllChildrenComment(
            @PathVariable String commentId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "newest") String order
    ) {
        if(commentId == null || commentId.isBlank())
            throw new IllegalArgumentException("Video id is null or blank");

        List<Comment> childCommentList = commentService.getAllChildrenComment(commentId, page, size, "oldest".equalsIgnoreCase(order));
        return ResponseEntity.ok(new APIResponseWithData<List<Comment>>(Boolean.TRUE, "Get all children comments of a comment successfully",
        HttpStatus.OK, childCommentList));
    }

    @GetMapping("/myVideosComments")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getMyVideosComments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "all") String repliedFilter,
            @RequestParam(required = false) String videoId
    ) {
        if(repliedFilter.equals("replied"))
            return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get all my videos's comments(replied) successfully", HttpStatus.OK,
                    commentService.getAllCommentFromMyVideosThatCurrentUserReplied(videoId)));
        else if(repliedFilter.equals("not-replied"))
            return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get all my videos's comments(not replied) successfully", HttpStatus.OK,
                    commentService.getAllCommentFromMyVideosThatCurrentUserNotReplied(videoId)));

        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get all my videos's comments successfully", HttpStatus.OK,
        commentService.getAllCommentFromMyVideos(videoId, page, size)));
    }

    @PutMapping()
    public ResponseEntity<APIResponseWithData<Comment>> updateAComment(@RequestBody Comment comment) {
        if(comment == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        return ResponseEntity.ok(new APIResponseWithData<Comment>(Boolean.TRUE, "Updated a comment successfully", HttpStatus.OK,
        commentService.updateComment(comment)));
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

    @GetMapping("/my")
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllCommentOfCurentUser() {
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "get all my comments successfully",
                HttpStatus.OK, commentService.getAllCommentOfCurrentUser()));
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
