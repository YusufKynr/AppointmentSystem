package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUser(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(int id, User updatedUser) {
        User existUser = getUser(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: id=" + id));
        if (updatedUser.getPassword() != null) {
            existUser.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getPassword() != null) {
            existUser.setUserId(updatedUser.getUserId());
        }

        return userRepository.save(existUser);
    }

    public User saveUser(User user) {
        if (userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("Kullanıcı zaten mevcut: id=" + user.getUserId());
        }
        return userRepository.save(user);
    }

    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Kullanıcı bulunamadı: id=" + userId);
        }
        userRepository.deleteById(userId);
    }


}
