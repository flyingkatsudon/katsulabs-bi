package com.insightboard.application.cboard;

import com.insightboard.api.cboard.ServiceStatusDto;
import com.insightboard.domain.metadata.DashboardUser;
import com.insightboard.infrastructure.persistence.DashboardUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CboardCommonsService {

    private final DashboardUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final Map<String, String> persistStore = new ConcurrentHashMap<>();

    public CboardCommonsService(
            DashboardUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    public ServiceStatusDto changePassword(String userId, String curPwd, String newPwd, String cfmPwd) {
        if (newPwd == null || !newPwd.equals(cfmPwd)) {
            return ServiceStatusDto.fail("Password confirmation does not match");
        }
        DashboardUser user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(curPwd, user.getUserPassword())) {
            return ServiceStatusDto.fail("Current password is incorrect");
        }
        user.setUserPassword(passwordEncoder.encode(newPwd));
        userRepository.save(user);
        return ServiceStatusDto.ok();
    }

    public String persist(String dataStr) {
        try {
            Map<String, Object> root = objectMapper.readValue(dataStr, new TypeReference<>() {});
            String persistId = String.valueOf(root.get("persistId"));
            Object data = root.get("data");
            persistStore.put(persistId, objectMapper.writeValueAsString(data));
            return persistId;
        } catch (Exception e) {
            return "";
        }
    }

    public String getPersistData(String persistId) {
        return persistStore.getOrDefault(persistId, "{}");
    }
}
