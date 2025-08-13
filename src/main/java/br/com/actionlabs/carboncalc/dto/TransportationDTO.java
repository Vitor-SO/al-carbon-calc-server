package br.com.actionlabs.carboncalc.dto;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransportationDTO {
  @NotBlank(message = "Transportation type is required")
  private TransportationType type;
  @NotNull(message = "Monthly distance is required")
  private int monthlyDistance;
}
