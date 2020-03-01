package com.plapp.authservice.entities;

import com.plapp.entities.auth.UserCredentials;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class ResourceAuthority implements GrantedAuthority, Serializable {
    @Id
    @GeneratedValue
    private Long authorityId;

    private String authority;

    @ElementCollection
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Long> values = new ArrayList<>();

    private Long userId;

    public ResourceAuthority(String authority, Long userId) {
        this.authority = authority;
        this.userId = userId;
    }

    public void addValue(Long value) {
        values.add(value);
    }
}
