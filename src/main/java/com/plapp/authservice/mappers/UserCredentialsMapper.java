package com.plapp.authservice.mappers;

import com.plapp.authservice.entities.ResourceAuthority;
import com.plapp.authservice.entities.UserCredentialsDPO;
import com.plapp.authservice.repositories.ResourceAuthorityRepository;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import com.plapp.authservice.services.UserCredentialsService;
import com.plapp.entities.auth.UserCredentials;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ResourceAuthorityRepository.class, UserCredentialsRepository.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserCredentialsMapper {
    @Mapping(target="authorities", source="id")
    UserCredentialsDPO userCredentialsToUserCredentialsDPO(UserCredentials userCredentials);
    UserCredentials userCredentialsDPOToUserCredentials(UserCredentialsDPO userCredentialsDPO);
}