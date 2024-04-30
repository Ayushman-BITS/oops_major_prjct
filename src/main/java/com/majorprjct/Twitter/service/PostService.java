package com.majorprjct.Twitter.service;

import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.entity.User;
import com.majorprjct.Twitter.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(String postBody, Integer userId) {
        User user = new User();
        user.setUserID(userId);
        Post post = new Post();
        post.setContent(postBody);
        post.setUser(user);
        post.setPostDate(LocalDate.now());
        return postRepository.save(post);
    }

    public Optional<Post> getPostById(Integer postID) {
        return postRepository.findByPostID(postID);
    }

    public Post updatePost(Integer postID, String postBody) {
        Optional<Post> postOptional = postRepository.findByPostID(postID);
        if (postOptional.isEmpty()) {
            return null;
        }
        Post post = postOptional.get();
        post.setContent(postBody);
        return postRepository.save(post);
    }

    public String deletePost(Integer postID) {
        Optional<Post> postOptional = postRepository.findByPostID(postID);
        if (postOptional.isEmpty()) {
            return "Post does not exist";
        }
        postRepository.deleteById(postID);
        return "Post deleted";
    }

    public List<Post> findAllByUser(User user) {
        return postRepository.findAllByUser(user);
    }

    public List<Post> findAllByUserSortedByPostID(User user) {
        List<Post> userPosts = postRepository.findAllByUser(user);
        return userPosts.stream()
                .sorted(Comparator.comparingInt(Post::getPostID).reversed()) // Sort by postID in descending order
                .collect(Collectors.toList());
    }

    public boolean findPostById(Integer postID) {
        return postRepository.findByPostID(postID).isPresent();
    }
}
