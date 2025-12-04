package org.example.service;

import org.example.dao.DAOUser;
import org.example.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final DAOUser dao = new DAOUser();

    public boolean register(String username, String password) {

        if (dao.find(username) != null) {
            return false;
        }

        String hashed = hash(password);
        dao.save(username, hashed, "user");
        return true;
    }

    public boolean signIn(String username, String password) {

        User user = dao.find(username);
        if (user == null) return false;

        return BCrypt.checkpw(password, user.getPassword());
    }

    public String getRole(String username) {
        User user = dao.find(username);
        return user != null ? user.getRole() : "guest";
    }

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
