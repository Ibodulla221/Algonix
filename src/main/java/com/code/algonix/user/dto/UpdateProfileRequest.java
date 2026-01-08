package com.code.algonix.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
    
    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;
    
    @Size(max = 100, message = "Company must be less than 100 characters")
    private String company;
    
    @Size(max = 100, message = "Job title must be less than 100 characters")
    private String jobTitle;
    
    @Size(max = 200, message = "Website URL must be less than 200 characters")
    private String website;
    
    @Size(max = 50, message = "GitHub username must be less than 50 characters")
    private String githubUsername;
    
    @Size(max = 200, message = "LinkedIn URL must be less than 200 characters")
    private String linkedinUrl;
    
    @Size(max = 50, message = "Twitter username must be less than 50 characters")
    private String twitterUsername;
    
    // Privacy settings
    private Boolean isProfilePublic;
    private Boolean showEmail;
    private Boolean showLocation;
    private Boolean showCompany;
}