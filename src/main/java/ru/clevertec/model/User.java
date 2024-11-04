package ru.clevertec.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private List<Contact> contacts;
}
