package ir.neshan.urlshortener.mapper;

import ir.neshan.urlshortener.domain.Link;
import ir.neshan.urlshortener.dto.CreateLinkDTO;
import ir.neshan.urlshortener.dto.LinkDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface LinkMapper {

    LinkDTO toDTO(Link link);

    @Mapping(target = "userId",source = "userId")
    @Mapping(target = "visitCount",constant = "0")
    Link fromCreateDTO(CreateLinkDTO createLinkDTO, UUID userId);

    Link toEntity(LinkDTO dto);
}
