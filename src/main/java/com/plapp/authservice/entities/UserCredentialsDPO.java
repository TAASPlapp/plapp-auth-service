package com.plapp.authservice.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UserCredentialsDPO {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String password;

    @OneToMany(cascade = {CascadeType.ALL},
               orphanRemoval = true)
    List<ResourceAuthority> authorities = new ArrayList<>();

    public void addResourceAuthority(ResourceAuthority authority) {
        authority.setUserId(this.id);
        authorities.add(authority);
    }
}
