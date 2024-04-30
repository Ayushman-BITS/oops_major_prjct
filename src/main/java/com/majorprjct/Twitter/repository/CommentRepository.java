package com.majorprjct.Twitter.repository;

import com.majorprjct.Twitter.entity.Comment;
import com.majorprjct.Twitter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByCommentID(Integer commentID);

    List<Comment> findAllByPost(Post post);
}
