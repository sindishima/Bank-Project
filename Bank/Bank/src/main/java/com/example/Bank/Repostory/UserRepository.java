package com.example.Bank.Repostory;

import com.example.Bank.Enum.Role;
import com.example.Bank.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findUserByRole(Role role);
}