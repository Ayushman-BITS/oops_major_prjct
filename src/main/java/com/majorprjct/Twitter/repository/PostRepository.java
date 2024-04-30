package com.majorprjct.Twitter.repository;

import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> findByPostID(Integer postID);

    List<Post> findAllByUser(User user);
}
