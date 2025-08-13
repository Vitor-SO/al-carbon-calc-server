package br.com.actionlabs.carboncalc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("users")
public class Users {
    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String uf;
}
