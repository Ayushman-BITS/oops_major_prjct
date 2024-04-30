package com.majorprjct.Twitter.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "comments")
public class Comment implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentID;
    private String commentBody;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentCreator;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "created_at")
    private LocalDateTime commentCreatedAt;

    public Comment() {
        // Default constructor required by JPA
    }

    public Comment(String commentBody, Post post, User commentCreator) {
        this.commentBody = commentBody;
        this.post = post;
        this.commentCreator = commentCreator;
        this.commentCreatedAt = LocalDateTime.now(); // Set current timestamp
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public User getCommentCreator() {
        return commentCreator;
    }

    public void setCommentCreator(User commentCreator) {
        this.commentCreator = commentCreator;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(LocalDateTime commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
    }
}
