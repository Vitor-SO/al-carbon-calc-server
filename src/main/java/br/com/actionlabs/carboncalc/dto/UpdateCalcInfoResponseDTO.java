package br.com.actionlabs.carboncalc.dto;

import lombok.Data;

@Data
public class UpdateCalcInfoResponseDTO {
  private boolean success;

  public UpdateCalcInfoResponseDTO(boolean success) {
    this.success = success;
  }
}
