package com.plapp.authservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plapp.authservice.services.AuthorizationService;
import com.plapp.entities.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/{userId}/update")
    public ApiResponse<String> updateAuthorization(@PathVariable Long userId,
                                                   @RequestParam String urlRegex,
                                                   @RequestParam Long value) {
        try {
            authorizationService.addResourceAuthorityValue(userId, urlRegex, value);
            String jwt = authorizationService.buildJwtWithAuthorizations(userId);
            return new ApiResponse<>(true, null, jwt);

        } catch (JsonProcessingException e) {
            return new ApiResponse<>(false,"Could not load user authorities");
        } catch (HibernateException e) {
            return new ApiResponse<>(false, "Could not update authority");
        }
    }

    @PostMapping("/{userId}/remove")
    public ApiResponse<String> removeAuthorization(@PathVariable long userId,
                                                   @RequestParam String urlRegex,
                                                   @RequestParam long value) {
        try {
            authorizationService.removeResourceAuthorityValue(userId, urlRegex, value);
            String jwt = authorizationService.buildJwtWithAuthorizations(userId);
            return new ApiResponse<>(true, null, jwt);

        } catch (JsonProcessingException e) {
            return new ApiResponse<>(false,"Could not load user authorities");
        } catch (HibernateException e) {
            return new ApiResponse<>(false, "Could not remove authority value");
        }
    }

}
