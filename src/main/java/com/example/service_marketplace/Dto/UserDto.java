package com.example.service_marketplace.Dto;

import com.example.service_marketplace.Entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    private String id;
    @NotBlank(message = "req")
    private String fullName;

    private String email;

    private Role role;

    private String phone;

    private String address;

    private String avatar;


    // add if missing
    private Boolean active;  // NOT boolean
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }




}
