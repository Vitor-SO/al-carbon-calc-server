package br.com.actionlabs.carboncalc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("userCarbonEmission")
public class UserCarbonEmission {
    @Id
    private String id;
    private double energy;
    private double transportation;
    private double solidWasteTotal;
    private double total;
}
