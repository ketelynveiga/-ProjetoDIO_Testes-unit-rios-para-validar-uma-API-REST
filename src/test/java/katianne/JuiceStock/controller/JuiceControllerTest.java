package katianne.JuiceStock.controller;

import katianne.JuiceStock.builder.JuiceDTOBuilder;
import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.dto.QuantityDTO;
import katianne.JuiceStock.exception.JuiceNotFoundException;
import katianne.JuiceStock.exception.JuiceStockExceededException;
import katianne.JuiceStock.service.JuiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static katianne.JuiceStock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public classJuiceControllerTest {

    private Juice static final StringJuice_API_URL_PATH = "/api/v1/Juices";
    private static final long VALID_Juice_ID = 1L;
    private static final long INVALID_Juice_ID = 2l;
    private static final String JuiceAPI_SUBPATH_INCREMENT_URL = "/increment";
    private static final String JuiceAPI_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private JuiceService JuiceService;

    @InjectMocks
    private JuiceController JuiceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(JuiceController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAJuiceIsCreated() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        // when
        when(JuiceService.createJuice(JuiceDTO)).thenReturn(JuiceDTO);

        // then
        mockMvc.perform(post(Juice_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(JuiceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(JuiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(JuiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(JuiceDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        JuiceDTO.setBrand(null);

        // then
        mockMvc.perform(post(Juice_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(JuiceDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        //when
        when(JuiceService.findByName(JuiceDTO.getName())).thenReturn(JuiceDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(Juice_API_URL_PATH + "/" + JuiceDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(JuiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(JuiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(JuiceDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        //when
        when(JuiceService.findByName(JuiceDTO.getName())).thenThrow(JuiceNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(Juice_API_URL_PATH + "/" + JuiceDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithJuicesIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        //when
        when(JuiceService.listAll()).thenReturn(Collections.singletonList(JuiceDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(Juice_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(JuiceDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(JuiceDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(JuiceDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutJuicesIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        //when
        when(JuiceService.listAll()).thenReturn(Collections.singletonList(JuiceDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(Juice_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        //when
        doNothing().when(JuiceService).deleteById(JuiceDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(Juice_API_URL_PATH + "/" +JuiceDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(JuiceNotFoundException.class).when(JuiceService).deleteById(INVALID_Juice_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(Juice_API_URL_PATH + "/" + INVALID_Juice_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

       JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());

        when(JuiceService.increment(VALID_Juice_ID, quantityDTO.getQuantity())).thenReturn(JuiceDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(JuiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(JuiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(JuiceDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(JuiceDTO.getQuantity())));
    }

//    @Test
//    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        JuiceDTOJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//       JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.increment(VALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceStockExceededException.class);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }

//    @Test
//    void whenPATCHIsCalledWithInvalidJuiceIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        when(JuiceService.increment(INVALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceNotFoundException.class);
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + INVALID_Juice_ID + Juice_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//       JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.decrement(VALID_Juice_ID, quantityDTO.getQuantity())).thenReturn(JuiceDTO);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID +Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(JuiceDTO.getName())))
//                .andExpect(jsonPath("$.brand", is(JuiceDTO.getBrand())))
//                .andExpect(jsonPath("$.type", is(JuiceDTO.getType().toString())))
//                .andExpect(jsonPath("$.quantity", is(JuiceDTO.getQuantity())));
//    }
//
//    @Test
//    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(60)
//                .build();
//
//        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//       JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.decrement(VALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceStockExceededException.class);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void whenPATCHIsCalledWithInvalidJuiceIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        when(JuiceService.decrement(INVALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceNotFoundException.class);
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + INVALID_Juice_ID + Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
}
