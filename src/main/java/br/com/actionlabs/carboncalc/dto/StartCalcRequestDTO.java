package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartCalcRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
  @NotBlank(message = "UF is required")
  private String uf;
  @NotBlank(message = "Phone number is required")
  private String phoneNumber;
}
