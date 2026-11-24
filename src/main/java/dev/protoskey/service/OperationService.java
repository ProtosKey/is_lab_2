package dev.protoskey.service;

import dev.protoskey.entity.Operation;
import dev.protoskey.entity.OperationStatus;
import dev.protoskey.entity.OperationType;
import dev.protoskey.entity.User;
import dev.protoskey.repository.OperationRepository;
import dev.protoskey.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OperationService {
    @Inject
    private OperationRepository operationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UpdateNotifier updateNotifier;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void recordSuccess(User user, OperationType type, int objectCount, String fileName) {
        Operation operation = new Operation();
        operation.setUser(refresh(user));
        operation.setType(type);
        operation.setStatus(OperationStatus.SUCCESS);
        operation.setObjectCount(objectCount);
        operation.setFileName(fileName);
        operationRepository.save(operation);
        updateNotifier.notify("operations");
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void recordFailure(User user, OperationType type, String fileName, String message) {
        Operation operation = new Operation();
        operation.setUser(refresh(user));
        operation.setType(type);
        operation.setStatus(OperationStatus.FAILED);
        operation.setMessage(message);
        operation.setFileName(fileName);
        operationRepository.save(operation);
        updateNotifier.notify("operations");
    }

    private User refresh(User user) {
        return userRepository.findById(user.getId()).orElse(user);
    }

    public java.util.List<Operation> historyFor(User user) {
        return user.getUserLevel() == dev.protoskey.entity.UserLevel.ADMIN
                ? operationRepository.findAll()
                : operationRepository.findAllByUser(user.getId());
    }

    public java.util.List<Operation> historyPageFor(User user, Integer cursor, int limit) {
        return user.getUserLevel() == dev.protoskey.entity.UserLevel.ADMIN
                ? operationRepository.findPage(cursor, limit)
                : operationRepository.findPageByUser(user.getId(), cursor, limit);
    }

    public long countFor(User user) {
        return user.getUserLevel() == dev.protoskey.entity.UserLevel.ADMIN
                ? operationRepository.countAll()
                : operationRepository.countByUser(user.getId());
    }
}
