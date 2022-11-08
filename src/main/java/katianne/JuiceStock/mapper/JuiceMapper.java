package katianne.JuiceStock.mapper;

import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.entity.Juice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JuiceMapper {

    JuiceMapper INSTANCE = Mappers.getMapper(JuiceMapper.class);

    Juice toModel(JuiceDTO JuiceDTO);

    JuiceDTO toDTO(Juice juice);
}
