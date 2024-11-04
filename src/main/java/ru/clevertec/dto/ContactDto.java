package ru.clevertec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.model.enums.ContactType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDto {
    private ContactType type;
    private String value;
}
