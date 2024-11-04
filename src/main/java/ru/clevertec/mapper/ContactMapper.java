package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.clevertec.dto.ContactDto;
import ru.clevertec.model.Contact;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    ContactDto toContactDto(Contact contact);

    @Mapping(target = "id", ignore = true)
    Contact toContact(ContactDto contactDto);
}
