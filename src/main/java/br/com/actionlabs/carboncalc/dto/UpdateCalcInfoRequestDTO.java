package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCalcInfoRequestDTO {
  @NotBlank(message = "Id is required")
  private String id;
  @NotNull(message = "Energy consumption is required")
  private int energyConsumption;
  private List<TransportationDTO> transportation;
  @NotNull(message = "Solid waste total is required")
  private int solidWasteTotal;
  @NotNull(message = "Recycle percentage is required")
  private double recyclePercentage;
}
