package com.weighing.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String username;
    private String passwordHash;
    private boolean accountExpired;
    private String role;
    private boolean isEnabled;
}
