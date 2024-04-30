package com.majorprjct.Twitter.service;

import com.majorprjct.Twitter.entity.Post;
import com.majorprjct.Twitter.entity.User;
import com.majorprjct.Twitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;

    @Autowired
    public UserService(UserRepository userRepository, PostService postService) {
        this.userRepository = userRepository;
        this.postService = postService;
    }



    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUserID(Integer userID) {
        return userRepository.findByUserID(userID);
    }
    public boolean existsByUserID(Integer userID) {
        return userRepository.existsByUserID(userID);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<Post> getUserFeed() {
        List<User> allUsers = findAllUsers();
        List<Post> allPosts = new ArrayList<>();

        for (User user : allUsers) {
            List<Post> userPosts = postService.findAllByUser(user);
            allPosts.addAll(userPosts);
        }

        // Sort all posts by postID in descending order
        allPosts.sort(Comparator.comparing(Post::getPostID).reversed());

        return allPosts;
    }


    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}

