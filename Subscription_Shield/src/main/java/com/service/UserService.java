package com.service;

import com.entity.User;

import java.util.List;

public interface UserService {

    boolean addUser(User user);

    List<User> getAllUsers();

    User getUserById(int id);

    boolean updateUser(User user);

    boolean deleteUser(int id);
}
