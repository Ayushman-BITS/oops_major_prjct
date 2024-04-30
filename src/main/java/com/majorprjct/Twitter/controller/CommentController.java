package com.majorprjct.Twitter.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.majorprjct.Twitter.entity.Comment;
import com.majorprjct.Twitter.service.CommentService;
import com.majorprjct.Twitter.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    static class ErrorResponse {
        @JsonProperty("Error")
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestBody CommentRequestBody requestBody) {
        Integer postID = requestBody.getPostID();
        Integer userID = requestBody.getUserID();
        String commentBody = requestBody.getCommentBody();

        if (!commentService.findByUserID(userID)) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        else if (!postService.findPostById(postID)) {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Comment comment = commentService.createComment(commentBody, postID, userID);
        return ResponseEntity.ok("Comment created successfully");
    }

    @GetMapping
    public ResponseEntity<Object> getCommentById(@RequestParam Integer commentID) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentID);
        if (commentOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Comment comment = commentOptional.get();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("commentID", comment.getCommentID());
        response.put("commentBody", comment.getCommentBody());

        ObjectNode commentCreatorNode = objectMapper.createObjectNode();
        commentCreatorNode.put("userID", comment.getCommentCreator().getUserID());
        commentCreatorNode.put("name", comment.getCommentCreator().getName());
        response.set("commentCreator", commentCreatorNode);

        return ResponseEntity.ok(response);
    }


    @PatchMapping
    public ResponseEntity<Object> updateComment(@RequestBody CommentRequestBody requestBody) {
        Integer commentID = requestBody.getCommentID();
        String commentBody = requestBody.getCommentBody();

        String result = String.valueOf(commentService.updateComment(commentID, commentBody));
        if (result.equals("Comment edited successfully")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Comment does not exist")) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteComment(@RequestParam Integer commentID) {
        String result = commentService.deleteComment(commentID);
        if (result.equals("Comment deleted")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Comment does not exist")) {
            ErrorResponse errorResponse = new ErrorResponse("Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.badRequest().body(result);
    }

    static class CommentRequestBody {
        private String commentBody;
        private Integer postID;
        private Integer userID;
        private Integer commentID;

        public String getCommentBody() {
            return commentBody;
        }

        public void setCommentBody(String commentBody) {
            this.commentBody = commentBody;
        }

        public Integer getPostID() {
            return postID;
        }

        public void setPostID(Integer postID) {
            this.postID = postID;
        }

        public Integer getUserID() {
            return userID;
        }

        public void setUserID(Integer userID) {
            this.userID = userID;
        }

        public Integer getCommentID() {
            return commentID;
        }

        public void setCommentID(Integer commentID) {
            this.commentID = commentID;
        }
    }
}