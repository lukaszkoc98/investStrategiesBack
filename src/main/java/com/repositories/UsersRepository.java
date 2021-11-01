package com.repositories;

import com.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<User, Integer> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update public.users set password = :new_password where id = :user_id", nativeQuery = true)
    @Transactional
    void updatePassword(String new_password, UUID user_id);
}
