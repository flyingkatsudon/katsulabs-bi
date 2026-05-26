package com.katsulabs.bi.adapter.web.domains.user;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.common.AccessControl;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.application.domains.user.DeleteUserUseCase;
import com.katsulabs.bi.application.domains.user.ListUsersUseCase;
import com.katsulabs.bi.application.domains.user.SaveUserUseCase;
import com.katsulabs.bi.application.domains.user.UpdateUserUseCase;
import com.katsulabs.bi.domain.domains.user.UserSummary;
import com.katsulabs.bi.domain.domains.user.UserWriteCommand;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final SaveUserUseCase saveUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final AccessControl accessControl;


    @GetMapping
    public List<UserResponse> list() {
        accessControl.requireManageUsers();
        return listUsersUseCase.execute().stream().map(UserController::toResponse).toList();
    }

    @PostMapping
    public ServiceResult create(@RequestBody UserWriteRequest request) {
        accessControl.requireManageUsers();
        return saveUserUseCase.execute(toCommand(request));
    }

    @PutMapping("/{userId}")
    public ServiceResult update(@PathVariable String userId, @RequestBody UserWriteRequest request) {
        accessControl.requireManageUsers();
        return updateUserUseCase.execute(toCommand(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ServiceResult delete(@PathVariable String userId) {
        accessControl.requireManageUsers();
        return deleteUserUseCase.execute(userId);
    }

    private static UserWriteCommand toCommand(UserWriteRequest request) {
        return new UserWriteCommand(
                request.userId(),
                request.loginName(),
                request.displayName(),
                request.roleId(),
                request.password());
    }

    private static UserWriteCommand toCommand(String userId, UserWriteRequest request) {
        return new UserWriteCommand(
                userId,
                request.loginName(),
                request.displayName(),
                request.roleId(),
                request.password());
    }

    private static UserResponse toResponse(UserSummary summary) {
        return new UserResponse(
                summary.userId(),
                summary.loginName(),
                summary.displayName(),
                summary.roleId(),
                summary.roleName(),
                summary.userStatus());
    }
}
