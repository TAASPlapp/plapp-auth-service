package com.plapp.authservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Session {
    @GeneratedValue
    @Id
    private long id;

    private String token;
    private String platform;
    private String login_ip;
}
