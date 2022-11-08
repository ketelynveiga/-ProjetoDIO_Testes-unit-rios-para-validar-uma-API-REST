package katianne.JuiceStock.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.enums.JuiceType;

@Builder
public  class JuiceDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Maguary";

    @Builder.Default
    private String brand = "Del Valle";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    privateJuiceType type = JuiceType.LAGER;

    public JuiceDTO toJuiceDTO() {
        return new JuiceDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
