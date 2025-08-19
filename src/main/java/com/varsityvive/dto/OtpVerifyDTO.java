package com.varsityvive.dto;

public class OtpVerifyDTO {
    private String name;
    private String email;
    private String password;
    private String department;
    private String batch;
    private String section;
    private String phoneNumber;
    private String otp;      // used by verify flow if you pass it along
    private String role;     // "student" | "admin" | "faculty"

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email == null ? null : email.trim().toLowerCase());
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getRole() { return role; }
    public void setRole(String role) {
        // Normalize to one of student/admin/faculty; default to student
        if (role == null || role.isBlank()) {
            this.role = "student";
        } else {
            this.role = role.trim().toLowerCase();
            switch (this.role) {
                case "admin":
                case "faculty":
                case "student":
                    break;
                default:
                    this.role = "student";
            }
        }
    }
}
