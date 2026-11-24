package dev.protoskey.service;

import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.repository.UserRepository;
import dev.protoskey.utils.Hasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository userRepository;

    public List<User> findAll(User current) throws AccessDeniedException {
        checkAdmin(current);
        return userRepository.findAll();
    }

    @Transactional(rollbackOn = Exception.class)
    public User changeLevel(Integer id, UserLevel level, User current)
            throws AccessDeniedException, EntityNotFoundException, ValidatorException {
        checkAdmin(current);
        if (level == null) {
            throw new ValidatorException("Не указан уровень доступа");
        }

        User target = find(id);
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getUserLevel() == UserLevel.ADMIN)
                .count();
        if (target.getUserLevel() == UserLevel.ADMIN && level != UserLevel.ADMIN && adminCount <= 1) {
            throw new AccessDeniedException("Нельзя разжаловать последнего администратора");
        }

        target.setUserLevel(level);
        return userRepository.update(target);
    }

    @Transactional(rollbackOn = Exception.class)
    public User changePassword(Integer id, String oldPassword, String newPassword, User current)
            throws AccessDeniedException, EntityNotFoundException, ValidatorException {
        boolean self = current.getId().equals(id);
        if (!self) {
            checkAdmin(current);
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidatorException("Новый пароль не может быть пустым");
        }

        User target = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь", id));
        if (self && (oldPassword == null || !Hasher.verify(oldPassword, target.getPasswordHash()))) {
            throw new ValidatorException("Неверный старый пароль");
        }

        target.setPasswordHash(Hasher.hash(newPassword));
        return userRepository.update(target);
    }

    private User find(Integer id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь", id));
    }

    private void checkAdmin(User current) throws AccessDeniedException {
        if (current.getUserLevel() != UserLevel.ADMIN) {
            throw new AccessDeniedException("Операция доступна только администратору");
        }
    }
}
