package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCalcInfoRequestDTO {
  @NotBlank(message = "Id is required")
  private String id;
  @NotBlank(message = "Energy consumption is required")
  private int energyConsumption;
  @NotBlank(message = "Transportation is required")
  private List<TransportationDTO> transportation;
  @NotBlank(message = "Solid waste total is required")
  private int solidWasteTotal;
  @NotBlank(message = "Recycle percentage is required")
  private double recyclePercentage;
}
