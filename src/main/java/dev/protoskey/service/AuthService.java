package dev.protoskey.service;

import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AuthenticationException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.LoginAlreadyTakenException;
import dev.protoskey.repository.UserRepository;
import dev.protoskey.utils.Hasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {
    @Inject
    private UserRepository userRepository;

    @Transactional(rollbackOn = Exception.class)
    public User register(String login, String password) throws LoginAlreadyTakenException {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new LoginAlreadyTakenException(login);
        }

        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(Hasher.hash(password));
        user.setUserLevel(firstUserIsAdmin() ? UserLevel.ADMIN : UserLevel.USER);
        return userRepository.save(user);
    }

    public User login(String login, String password) throws AuthenticationException {
        return userRepository.findByLogin(login)
                .filter(user -> Hasher.verify(password, user.getPasswordHash()))
                .orElseThrow(AuthenticationException::new);
    }

    public User findByLogin(String login) throws EntityNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь", login));
    }

    private boolean firstUserIsAdmin() {
        return userRepository.findAll().isEmpty();
    }
}
