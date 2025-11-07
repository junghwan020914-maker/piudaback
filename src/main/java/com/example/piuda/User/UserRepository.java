package com.example.piuda.User;

import com.example.piuda.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserEmail(String userEmail);
    // 이메일 중복 확인을 위한 메소드
    Optional<User> findByUserEmail(String userEmail);
}
