package com.nokia.rediscache.service;


import com.nokia.rediscache.dao.UserDao;
import com.nokia.rediscache.entities.UserInfo;
import com.nokia.rediscache.request.CreateUserRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class UserService implements InitializingBean {

    @Autowired
    UserDao dao;

    @Autowired
    RedisTemplate<String , Object> redisTemplate;


    public UserInfo createUser(CreateUserRequestDto userRequestDto) {
        UserInfo userInfo = userRequestDto.toUser();
        userInfo.setPassword(userInfo.getPassword());
        dao.save(userInfo);
        return userInfo;
    }


    /**
     * 10000 of records
     * --> (0,20) (21,40)
     * <p>
     * limit 20
     * limit 21 40
     *
     * @return
     */
    public List<UserInfo> fetchAllUsers(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 20);
        return dao.findAll(pageable).getContent();
    }

    public Optional<UserInfo> fetchOneById(String email) {



        /**
         * check if data is present in the redis
         *      --> use that
         *
         *      --> else  fetch from DB
         *      --> persist in redis
         *      and return
         */


        Object result = redisTemplate.opsForValue().get(fetchKey(email));

        if(Objects.nonNull(result)){
            return Optional.of((UserInfo)result);
        }

        Optional<UserInfo> byEmail = dao.findByEmail(email);
        if(byEmail.isPresent()){
            redisTemplate.opsForValue().set(fetchKey(email),byEmail.get(),Duration.ofMinutes(1));
        }
        return byEmail;
    }

    public String fetchKey(String email){
        return "userCache::"+email;
    }

    private String fetchUserKey(String email){
        return "userCache::" + email;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
