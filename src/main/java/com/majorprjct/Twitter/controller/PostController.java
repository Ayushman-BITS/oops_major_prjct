package com.majorprjct.Twitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.majorprjct.Twitter.entity.Comment;
import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.service.PostService;
import com.majorprjct.Twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Object> createPost(@RequestBody PostRequestBody requestBody) {
        String postBody = requestBody.getPostBody();
        Integer userID = requestBody.getUserID();

    // Check if the user exists before creating the post
        if (!userService.existsByUserID(userID)) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Post createdPost = postService.createPost(postBody, userID);
        return ResponseEntity.ok("Post created successfully");
    }


    @GetMapping
    public ResponseEntity<Object> getPostById(@RequestParam Integer postID) {
        Optional<Post> postOptional = postService.getPostById(postID);
        if (postOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        Post post = postOptional.get();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("postID", post.getPostID());
        response.put("postBody", post.getContent());
        response.put("date", post.getPostDate().toString());
        // Include comments if any
        List<Comment> comments = post.getComments();
        if (comments != null && !comments.isEmpty()) {
            ArrayNode commentsArray = objectMapper.createArrayNode();
            for (Comment comment : comments) {
                ObjectNode commentNode = objectMapper.createObjectNode();
                commentNode.put("commentID", comment.getCommentID());
                commentNode.put("commentBody", comment.getCommentBody());
                ObjectNode commentCreatorNode = objectMapper.createObjectNode();
                commentCreatorNode.put("userID", comment.getCommentCreator().getUserID());
                commentCreatorNode.put("name", comment.getCommentCreator().getName());
                commentNode.set("commentCreator", commentCreatorNode);
                commentsArray.add(commentNode);
            }
            response.set("comments", commentsArray);
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<Object> updatePost(@RequestBody PostRequestBody requestBody) {
        Integer postID = requestBody.getPostID();
        String postBody = requestBody.getPostBody();

        Post updatedPost = postService.updatePost(postID, postBody);
        if (updatedPost == null) {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok("Post edited successfully");
    }

    @DeleteMapping
    public ResponseEntity<Object> deletePost(@RequestParam Integer postID) {
        String result = postService.deletePost(postID);
        if (result.equals("Post does not exist")) {
            ErrorResponse errorResponse = new ErrorResponse("Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(result);
    }

    static class PostRequestBody {
        private String postBody;
        private Integer userID;
        private Integer postID;

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }

        public Integer getUserID() {
            return userID;
        }

        public void setUserID(Integer userID) {
            this.userID = userID;
        }

        public Integer getPostID() {
            return postID;
        }

        public void setPostID(Integer postID) {
            this.postID = postID;
        }
    }
}