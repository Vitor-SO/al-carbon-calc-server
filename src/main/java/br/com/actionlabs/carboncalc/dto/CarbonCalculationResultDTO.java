package br.com.actionlabs.carboncalc.dto;

import lombok.Data;

@Data
public class CarbonCalculationResultDTO {
  private double energy;
  private double transportation;
  private double solidWaste;
  private double total;

  public CarbonCalculationResultDTO(double energy, double transportation, double solidWaste, double total) {
    this.energy = energy;
    this.transportation = transportation;
    this.solidWaste = solidWaste;
    this.total = total;
  }
}
