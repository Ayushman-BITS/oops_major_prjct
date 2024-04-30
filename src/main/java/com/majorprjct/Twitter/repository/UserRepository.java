package com.majorprjct.Twitter.repository;

import com.majorprjct.Twitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByUserID(Integer userID);

    User findByUserIDAndEmail(Integer userID, String email);

    User findByNameAndEmail(String name, String email);

    User findByEmailAndPassword(String email, String password);

    List<User> findAll();

    boolean existsByUserID(Integer userID);
}

