package stream.flow.videoservice.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.mapper.UserMapper;
import stream.flow.videoservice.model.dto.request.UserRequest;
import stream.flow.videoservice.model.dto.response.UserResponse;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.repository.UsersRepository;
import stream.flow.videoservice.service.user.UserService;
import stream.flow.videoservice.util.SecurityUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public UserResponse syncUser() {

        UserRequest userRequest = SecurityUtils.getCurrentUser();

        log.info("Sync user with keycloak ID: {}", userRequest.getKeycloakId());

        Users user = usersRepository.findByKeycloakId(
                        userRequest.getKeycloakId())
                .orElse(null);

        if (user != null) {
            log.info("User exists with Keycloak ID: {}", user.getKeycloakId());
            return userMapper.toResponse(user);
        }

        user = userMapper.toEntity(userRequest);
        usersRepository.save(user);

        log.info("User created and sync: {}", user.getKeycloakId());

        return userMapper.toResponse(user);
    }
}
