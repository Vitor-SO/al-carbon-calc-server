package br.com.actionlabs.carboncalc.dto;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class StartCalcResponseDTO {
  private String id;
  private HttpStatusCode status;
  private String message;

  public StartCalcResponseDTO(String id, HttpStatusCode status, String message) {
    this.id = id;
    this.status = status;
    this.message = message;
  }
}
