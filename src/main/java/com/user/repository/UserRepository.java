package com.user.repository;

import com.repository.BaseRepository;
import com.user.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {

    User findByUsername(String username);

}
