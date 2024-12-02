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
    public ResponseEntity<APIResponseWithData<List<Comment>>> getAllCommentByVideoId(@PathVariable String videoId) {
        if(videoId == null || videoId.isBlank())
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<>(Boolean.FALSE, "Video id is null or blank", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<List<Comment>>(Boolean.TRUE, "Get all comment in a video successfully", HttpStatus.OK,
        commentService.getAllCommentByVideoId(videoId)));
    }

    @PatchMapping()
    public ResponseEntity<APIResponseWithData<Comment>> updateAComment(@RequestBody Comment comment) {
        if(comment == null)
            return  ResponseEntity.status(400).body(
                    new APIResponseWithData<Comment>(Boolean.FALSE, "Comment is null or empty", HttpStatus.BAD_REQUEST, null)
            );
        return ResponseEntity.ok(new APIResponseWithData<Comment>(Boolean.TRUE, "Updated a comment successfully", HttpStatus.OK, commentService.updateComment(comment)));
    }
}
