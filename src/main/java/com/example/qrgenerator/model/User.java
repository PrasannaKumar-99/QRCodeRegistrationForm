package com.example.qrgenerator.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min=3, max=50, message="Name must be between 3-50 characters")
    private String name;

    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    private int age;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid mobile number")
    

    @Column( nullable = false, unique = true)
    @NotNull
    private String mobileNumber;

    @NotBlank(message = "Email is mandatory")
    
    @Email(message = "Invalid email format")

    @Column( nullable = false, unique = true)
    @NotNull
    private String email;

    @NotBlank(message = "College name is mandatory")
    @Size(min=5, max=100, message="College name must be 5-100 characters")
    private String college;

    @NotBlank(message = "Address is mandatory")
    @Size(min=10, max=200, message="Address must be 10-200 characters")
    private String address;

    // Getters and Setters
    // Constructor
}
