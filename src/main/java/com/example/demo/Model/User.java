package com.example.demo.Model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password; // BCrypt hashed — never plain text

    private String role; // "TEACHER" or "STUDENT"

    private String name; // display name

    private String studentId; // only for STUDENT role
    // links to students.rollNumber

    public User() {}

    public User(String id, String username, String password,
                String role, String name, String studentId) {
        this.id        = id;
        this.username  = username;
        this.password  = password;
        this.role      = role;
        this.name      = name;
        this.studentId = studentId;
    }

    public String getId()        { return id; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getRole()      { return role; }
    public String getName()      { return name; }
    public String getStudentId() { return studentId; }

    //** For teachers to return own id
    public String getTeacherId()  {
        return "TEACHER".equals(role) ? id : null;
    }

    public void setId(String id)               { this.id = id; }
    public void setUsername(String username)   { this.username = username; }
    public void setPassword(String password)   { this.password = password; }
    public void setRole(String role)           { this.role = role; }
    public void setName(String name)           { this.name = name; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() { return Objects.hash(id, username); }
}