package com.majorprjct.Twitter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.majorprjct.Twitter.entity.Comment;
import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.entity.User;
import com.majorprjct.Twitter.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    public Comment createComment(String commentBody, Integer postId, Integer userId) {
        Optional<Post> postOptional = postService.getPostById(postId);
        Optional<User> userOptional = Optional.ofNullable(userService.findByUserID(userId));


        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            return null;
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        Comment comment = new Comment(commentBody, post, user);
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Integer id) {
        return commentRepository.findByCommentID(id);
    }

    public String updateComment(Integer commentID, String commentBody) {
        Optional<Comment> commentOptional = commentRepository.findByCommentID(commentID);

        if (commentOptional.isEmpty()) {
            return "Comment does not exist";
        }

        Comment comment = commentOptional.get();
        comment.setCommentBody(commentBody);
        commentRepository.save(comment);
        return "Comment edited successfully";
    }

    public String deleteComment(Integer commentID) {
        Optional<Comment> commentOptional = commentRepository.findByCommentID(commentID);

        if (commentOptional.isEmpty()) {
            return "Comment does not exist";
        }

        commentRepository.deleteById(commentID);
        return "Comment deleted";
    }

    public boolean findByUserID(Integer userID) {
        return userService.findByUserID(userID) != null;
    }
}

