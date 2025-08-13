package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.Users;
import br.com.actionlabs.carboncalc.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
public class OpenRestController {
  private final UsersService usersService;

  @Operation(summary = "Start a new calculation")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successful operation",
                  content = @Content(mediaType = "application/json",
                          array = @ArraySchema(schema = @Schema(implementation = Users.class))))
  })
  @PostMapping("start-calc")
  public ResponseEntity<StartCalcResponseDTO> startCalculation(
      @RequestBody StartCalcRequestDTO request) {
    try {
      return ResponseEntity.ok(new StartCalcResponseDTO(usersService.saveUser(request), HttpStatusCode.valueOf(200), "Success"));
    } catch (ResponseStatusException e) {
      return ResponseEntity.status(e.getStatusCode()).body(new StartCalcResponseDTO("",e.getStatusCode(),e.getMessage()));
    }
  }

  @PutMapping("info")
  public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
      @RequestBody UpdateCalcInfoRequestDTO request) {
    throw new RuntimeException("Not implemented");
  }

  @GetMapping("result/{id}")
  public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
    throw new RuntimeException("Not implemented");
  }
}
