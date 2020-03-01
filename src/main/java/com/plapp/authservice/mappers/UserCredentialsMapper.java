package com.plapp.authservice.mappers;

import com.plapp.authservice.repositories.ResourceAuthorityRepository;
import com.plapp.authservice.repositories.UserCredentialsRepository;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {ResourceAuthorityRepository.class, UserCredentialsRepository.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserCredentialsMapper {
    //@Mapping(target="authorities", source="id")
    //UserCredentialsDPO userCredentialsToUserCredentialsDPO(UserCredentials userCredentials);
    //UserCredentials userCredentialsDPOToUserCredentials(UserCredentialsDPO userCredentialsDPO);
}