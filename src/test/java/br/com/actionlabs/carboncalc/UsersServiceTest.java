package br.com.actionlabs.carboncalc;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.model.Users;
import br.com.actionlabs.carboncalc.repository.UsersRepository;
import br.com.actionlabs.carboncalc.services.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

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
}
