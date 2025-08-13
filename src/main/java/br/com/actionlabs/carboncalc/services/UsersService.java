package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.model.Users;
import br.com.actionlabs.carboncalc.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private static final String PHONE_REGEX = "^[0-9]{11}$"; // verifica se o número possui 11 dígitos
    private final UsersRepository usersRepository;

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

    public boolean isEmailAlreadyExists(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);

        return user.isPresent();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_REGEX);
    }
}
