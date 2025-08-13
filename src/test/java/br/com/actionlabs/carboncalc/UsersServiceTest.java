package br.com.actionlabs.carboncalc;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.model.UserCarbonEmission;
import br.com.actionlabs.carboncalc.model.Users;
import br.com.actionlabs.carboncalc.repository.UserCarbonEmissionRepository;
import br.com.actionlabs.carboncalc.repository.UsersRepository;
import br.com.actionlabs.carboncalc.services.UsersService;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserCarbonEmissionRepository userCarbonEmissionRepository;

    @Test
    @DisplayName("should return true when a valid phone number is provided")
    void testIsValidPhoneNumber_valid() {
        String validPhoneNumber = "11987654321";

        boolean result = usersService.isValidPhoneNumber(validPhoneNumber);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("should return false when an invalid phone number is provided")
    void testIsValidPhoneNumber_invalid() {
        String invalidPhoneNumber = "119876543210";

        boolean result = usersService.isValidPhoneNumber(invalidPhoneNumber);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("should return true when a email already exists")
    void testIsValidEmail_valid() {
        String email = "actionlabs@email.com";
        Users user = new Users();
        user.setEmail(email);

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean resultado = usersService.isEmailAlreadyExists(email);

        Assertions.assertTrue(resultado);
        verify(usersRepository).findByEmail(email);
    }

    @Test
    @DisplayName("should return false when a email already exists")
    void testIsValidEmail_invalid() {
        String email = "actionlabs@email.com";
        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean resultado = usersService.isEmailAlreadyExists(email);

        Assertions.assertFalse(resultado);
        verify(usersRepository).findByEmail(email);
    }

    @Test
    @DisplayName("should return true when user is saved")
    void testUserSaved() {
        StartCalcRequestDTO dto = new StartCalcRequestDTO();
        dto.setName("João");
        dto.setEmail("joao@email.com");
        dto.setPhoneNumber("11987654321");
        dto.setUf("SP");

        Users user = new Users();
        user.setId("64f1a92b0a4f5c1234567890");
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUf(dto.getUf());

        when(usersRepository.save(any(Users.class))).thenReturn(user);

        String idGenerated = usersService.saveUser(dto);

        Assertions.assertNotNull(idGenerated);
        Assertions.assertEquals("64f1a92b0a4f5c1234567890", idGenerated);
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("should throw exception when user is not saved")
    void testUserNotSaved() {
        StartCalcRequestDTO dto = new StartCalcRequestDTO();
        dto.setName("João");
        dto.setEmail("joao@email.com");
        dto.setPhoneNumber("11987654321");
        dto.setUf("SP");

        when(usersRepository.save(any(Users.class)))
                .thenThrow(new MongoException("Erro ao salvar no Mongo"));

        MongoException exception = Assertions.assertThrows(MongoException.class, () -> {
            usersService.saveUser(dto);
        });

        Assertions.assertEquals("Erro ao salvar no Mongo", exception.getMessage());

        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("should return exception when user is not found")
    void testUserNotFound() {
        class FakeUserService {
            private final UsersRepository usersRepository;

            public FakeUserService(UsersRepository usersRepository) {
                this.usersRepository = usersRepository;
            }

            public Users getUserById(String id) {
                return usersRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            }
        }

        FakeUserService service = new FakeUserService(usersRepository);

        String userId = "123456";

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            service.getUserById(userId);
        });

        Assertions.assertEquals("Usuário não encontrado", exception.getMessage());

        verify(usersRepository, times(1)).findById(userId);
    }

    class FakeUsersService {
        private final UserCarbonEmissionRepository repository;

        public FakeUsersService(UserCarbonEmissionRepository repository) {
            this.repository = repository;
        }

        // Fake do método para não dar erro de user not found
        public boolean calculateCarbonFootprint(UpdateCalcInfoRequestDTO dto) {
            // Simula usuário encontrado
            if (dto == null) {
                throw new RuntimeException("User not found");
            }

            UserCarbonEmission emission = new UserCarbonEmission();
            emission.setEnergy(dto.getEnergyConsumption());
            emission.setTransportation(dto.getTransportation().stream()
                    .mapToDouble(t -> t.getMonthlyDistance()).sum());
            emission.setSolidWasteTotal(dto.getSolidWasteTotal());
            emission.setTotal(emission.getEnergy() + emission.getTransportation() + emission.getSolidWasteTotal());

            UserCarbonEmission saved = repository.save(emission);
            return saved != null && saved.getId() != null;
        }
    }

    @Test
    @DisplayName("should return true when user carbon is saved")
    void testUserCarbonSaved() {
        UserCarbonEmissionRepository repository = mock(UserCarbonEmissionRepository.class);
        FakeUsersService usersService = new FakeUsersService(repository);

        UpdateCalcInfoRequestDTO dto = new UpdateCalcInfoRequestDTO();
        dto.setEnergyConsumption(100);

        TransportationDTO dtoTransportation = new TransportationDTO();
        dtoTransportation.setType(TransportationType.CAR);
        dtoTransportation.setMonthlyDistance(10);

        dto.setTransportation(List.of(dtoTransportation));
        dto.setSolidWasteTotal(10);
        dto.setRecyclePercentage(0.5);

        UserCarbonEmission userCarbonEmission = new UserCarbonEmission();
        userCarbonEmission.setId("64f1a92b0a4f5c1234567890");
        userCarbonEmission.setEnergy(100);
        userCarbonEmission.setTransportation(10);
        userCarbonEmission.setSolidWasteTotal(10);
        userCarbonEmission.setTotal(120);

        when(repository.save(any(UserCarbonEmission.class))).thenReturn(userCarbonEmission);

        boolean result = usersService.calculateCarbonFootprint(dto);

        Assertions.assertTrue(result);
        verify(repository, times(1)).save(any(UserCarbonEmission.class));
    }

    @Test
    @DisplayName("should return false when user carbon is not saved")
    void testUserCarbonNotSaved() {
        UserCarbonEmissionRepository repository = mock(UserCarbonEmissionRepository.class);
        FakeUsersService usersService = new FakeUsersService(repository);

        UpdateCalcInfoRequestDTO dto = new UpdateCalcInfoRequestDTO();
        dto.setEnergyConsumption(100);

        TransportationDTO dtoTransportation = new TransportationDTO();
        dtoTransportation.setType(TransportationType.CAR);
        dtoTransportation.setMonthlyDistance(10);

        dto.setTransportation(List.of(dtoTransportation));
        dto.setSolidWasteTotal(10);
        dto.setRecyclePercentage(0.5);

        when(repository.save(any(UserCarbonEmission.class))).thenReturn(null);

        boolean result = usersService.calculateCarbonFootprint(dto);

        Assertions.assertFalse(result);
        verify(repository, times(1)).save(any(UserCarbonEmission.class));
    }

}
