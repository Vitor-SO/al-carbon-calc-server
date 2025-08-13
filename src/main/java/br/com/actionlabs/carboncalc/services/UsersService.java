package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.model.*;
import br.com.actionlabs.carboncalc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private static final String PHONE_REGEX = "^[0-9]{11}$"; // verifica se o número possui 11 dígitos
    private final UsersRepository usersRepository;
    private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
    private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;
    private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;
    private final UserCarbonEmissionRepository userCarbonEmissionRepository;

    public String saveUser(StartCalcRequestDTO userDto){
            boolean userEmailAlreadyExists = isEmailAlreadyExists(userDto.getEmail());

            if(userEmailAlreadyExists){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already exists");
            }

            if(!isValidPhoneNumber(userDto.getPhoneNumber())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
            }

            Users user = new Users();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setUf(userDto.getUf().toUpperCase());
            user.setPhoneNumber(userDto.getPhoneNumber());
            return usersRepository.save(user).getId();
    }

    public boolean calculateCarbonFootprint(UpdateCalcInfoRequestDTO userDto) {
        Optional<Users> user = usersRepository.findById(userDto.getId());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        String userUf = user.get().getUf();

        double energyEmission = calcEnergyEmission(userDto.getEnergyConsumption(), userUf);
        double transportationEmission = calcTransportationEmission(userDto.getTransportation());
        double solidWasteEmission = calcSolidWasteEmission(userUf,userDto.getSolidWasteTotal(), userDto.getRecyclePercentage());

        UserCarbonEmission carbonEmission = userCarbonEmissionRepository.findById(userDto.getId())
                .orElse(new UserCarbonEmission());
        carbonEmission.setId(userDto.getId());
        carbonEmission.setEnergy(energyEmission);
        carbonEmission.setTransportation(transportationEmission);
        carbonEmission.setSolidWasteTotal(solidWasteEmission);
        carbonEmission.setTotal(energyEmission + transportationEmission + solidWasteEmission);

        userCarbonEmissionRepository.save(carbonEmission);

        return true;
    }

    public UserCarbonEmission getUserCarbonEmission(String id) {
        Optional<UserCarbonEmission> userCarbonEmission = userCarbonEmissionRepository.findById(id);

        if (userCarbonEmission.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Carbon Emission not found");
        }

        return userCarbonEmission.get();
    }

    private double calcSolidWasteEmission(String userUf, int solidWasteTotal, double recyclePercentage) {
        Optional<SolidWasteEmissionFactor> emissionFactor = solidWasteEmissionFactorRepository.findById(userUf);

        if (emissionFactor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solid waste emission factor not found");
        }

        double recyclableWaste = solidWasteTotal * recyclePercentage;
        double nonRecyclableWaste = solidWasteTotal * (1 - recyclePercentage);
        double recyclableEmission = recyclableWaste * emissionFactor.get().getRecyclableFactor();
        double nonRecyclableEmission = nonRecyclableWaste * emissionFactor.get().getNonRecyclableFactor();

        return recyclableEmission + nonRecyclableEmission;
    }

    public double calcTransportationEmission(List<TransportationDTO> transportation) {
        return transportation.stream()
                .filter(t -> t.getMonthlyDistance() > 0)
                .mapToDouble(t -> {
                    TransportationEmissionFactor factorData = transportationEmissionFactorRepository
                            .findById(t.getType())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Transportation emission factor not found for type: " + t.getType()
                            ));

                    return t.getMonthlyDistance() * factorData.getFactor();
                })
                .sum();
    }

    public double calcEnergyEmission(double energyConsumption, String uf) {
        Optional<EnergyEmissionFactor> emissionFactor = energyEmissionFactorRepository.findByUf(uf);

        if (emissionFactor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Energy emission factor not found");
        }

        return energyConsumption * emissionFactor.get().getFactor();
    }


    public boolean isEmailAlreadyExists(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);

        return user.isPresent();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_REGEX);
    }
}
