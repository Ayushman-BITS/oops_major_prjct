package com.majorprjct.Twitter.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.majorprjct.Twitter.entity.Comment;
import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.entity.User;
import com.majorprjct.Twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.majorprjct.Twitter.dto.SignupRequest;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody SignupRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        User existingUser = userService.findByEmail(email);
        if (existingUser == null) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (!existingUser.getPassword().equals(password)) {
            ErrorResponse errorResponse = new ErrorResponse("Username/Password Incorrect");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok("Login Successful");
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignupRequest request) {
        String email = request.getEmail();
        User existingUser = userService.findByEmail(email);
        if (existingUser != null) {
            ErrorResponse errorResponse = new ErrorResponse("Forbidden, Account already exists");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setName(request.getName());
        newUser.setPassword(request.getPassword());

        userService.save(newUser);
        return ResponseEntity.ok("Account Creation Successful");
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUser(@RequestParam Integer userID) {
        User user = userService.findByUserID(userID);
        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        } else {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("name", user.getName());
            userNode.put("userID", user.getUserID());
            userNode.put("email", user.getEmail());
            return ResponseEntity.ok(userNode);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers() {
        ObjectNode[] userNodes = userService.findAllUsers().stream().map(user -> {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("name", user.getName());
            userNode.put("userID", user.getUserID());
            userNode.put("email", user.getEmail());
            return userNode;
        }).toArray(ObjectNode[]::new);
        return ResponseEntity.ok(userNodes);
    }

    @GetMapping("/")
    public ResponseEntity<Object> getUserFeed() {
        List<Post> userFeed = userService.getUserFeed();
        if (userFeed == null || userFeed.isEmpty()) {
            return ResponseEntity.ok(new ObjectNode[0]); // Return an empty array if no posts
        }

        // Sort the userFeed list based on postID (latest post first)
        userFeed.sort(Comparator.comparing(Post::getPostID).reversed());

        ObjectNode[] feedNodes = userFeed.stream().map(post -> {
            ObjectNode feedNode = objectMapper.createObjectNode();
            feedNode.put("postID", post.getPostID());
            feedNode.put("postBody", post.getContent());
            feedNode.put("date", post.getPostDate().toString());

            List<Comment> comments = post.getComments();
            if (comments != null && !comments.isEmpty()) {
                ArrayNode commentsArray = objectMapper.createArrayNode();
                for (Comment comment : comments) {
                    ObjectNode commentNode = objectMapper.createObjectNode();
                    commentNode.put("commentID", comment.getCommentID());
                    commentNode.put("commentBody", comment.getCommentBody());

                    User commentCreator = comment.getCommentCreator();
                    if (commentCreator != null) {
                        ObjectNode creatorNode = objectMapper.createObjectNode();
                        creatorNode.put("userID", commentCreator.getUserID());
                        creatorNode.put("name", commentCreator.getName());
                        commentNode.set("commentCreator", creatorNode);
                    }

                    commentsArray.add(commentNode);
                }
                feedNode.set("comments", commentsArray);
            }

            return feedNode;
        }).toArray(ObjectNode[]::new);

        return ResponseEntity.ok(feedNodes);
    }

}


