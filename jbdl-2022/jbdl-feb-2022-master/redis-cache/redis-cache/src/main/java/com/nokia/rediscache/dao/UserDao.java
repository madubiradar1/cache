package com.nokia.rediscache.dao;



import com.nokia.rediscache.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserDao extends JpaRepository<UserInfo, Long> {

//    @Cacheable(cacheNames = "userCache")
    Optional<UserInfo> findByEmail(String email);

}
