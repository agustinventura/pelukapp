package com.spanishcoders.model.dto;

import com.spanishcoders.model.Role;
import com.spanishcoders.model.User;
import com.spanishcoders.model.UserStatus;

/**
 * Created by agustin on 26/09/16.
 */
public class SignInUserDTO {

    private final Role role;
    private final Integer id;
    private final String name;
    private final String username;
    private final UserStatus status;

    public SignInUserDTO() {
        this.role = Role.CLIENT;
        this.id = 0;
        this.name = "";
        this.username = "";
        this.status = UserStatus.DELETED;
    }

    public SignInUserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.role = Role.getRole(user);
    }

    public Role getRole() {
        return role;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public UserStatus getStatus() {
        return status;
    }
}
