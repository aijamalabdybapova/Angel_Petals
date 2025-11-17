// entity/UserRole.java
package com.flowershop.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class UserRole extends BaseEntity { // Добавляем наследование

    public enum RoleName {
        ROLE_USER,

        ROLE_ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, unique = true, nullable = false)
    private RoleName name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    // Constructors
    public UserRole() {}

    public UserRole(RoleName name) {
        this.name = name;
    }

    public UserRole(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}