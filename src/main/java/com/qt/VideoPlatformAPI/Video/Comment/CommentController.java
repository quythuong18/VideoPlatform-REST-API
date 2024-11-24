package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    @PostMapping("/{videoId}")
    public ResponseEntity<APIResponseWithData<Comment>> addAComment(@PathVariable  String videoId,
        @RequestBody Comment comment) {
        return null;
    }
    @DeleteMapping("/{videoId}")
    public ResponseEntity<APIResponseWithData<Comment>> deleteAComment(@RequestParam String commentId) {
        return null;
    }
}
