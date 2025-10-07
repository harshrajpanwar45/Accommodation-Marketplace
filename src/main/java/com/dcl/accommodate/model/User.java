package com.dcl.accommodate.model;

import com.dcl.accommodate.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userId")
    private UUID userId;

    @Column(name = "firstName",nullable = false)
    private String firstName;

    @Column(name = "lastName",nullable = false)
    private String lastName;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "role",nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "createdAt",nullable = false,updatable = false)
    @CreatedDate
    private Instant createdDate;

    @Column(name = "lastModifiedAt")
    @LastModifiedDate
    private Instant lastModifiedDate;

}
