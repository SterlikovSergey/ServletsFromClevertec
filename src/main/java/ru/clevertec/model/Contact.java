package ru.clevertec.model;

import lombok.Builder;
import lombok.Data;
import ru.clevertec.model.enums.ContactType;

@Data
@Builder
public class Contact {
    private Long id;
    private ContactType type;
    private String value;
}
