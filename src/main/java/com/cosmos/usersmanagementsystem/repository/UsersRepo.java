package com.cosmos.usersmanagementsystem.repository;


import com.cosmos.usersmanagementsystem.entity.OurUsers;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {

    Optional<OurUsers> findByEmail(String email);
    OurUsers findOurUsersById(Integer id);
    @Transactional
    @Modifying
    @Query("update OurUsers o set  o.password = ?2 where o.email = ?1 ")
    void updatePassword(String email, String password);

}
