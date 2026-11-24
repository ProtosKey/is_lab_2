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

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void recordSuccess(User user, OperationType type, int objectCount, String fileName) {
        Operation operation = new Operation();
        operation.setUser(refresh(user));
        operation.setType(type);
        operation.setStatus(OperationStatus.SUCCESS);
        operation.setObjectCount(objectCount);
        operation.setFileName(fileName);
        operationRepository.save(operation);
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
    }

    private User refresh(User user) {
        return userRepository.findById(user.getId()).orElse(user);
    }
}
