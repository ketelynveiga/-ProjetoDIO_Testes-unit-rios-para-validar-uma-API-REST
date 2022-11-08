package katianne.JuiceStock.service;

import katianne.juiceStock.builder.JuiceDTOBuilder;
import katianne.juiceStock.dto.JuiceDTO;
import katianne.JuiceStock.entity.Juice;
import katianne.JuiceStock.exception.JuiceAlreadyRegisteredException;
import katianne.JuiceStock.exception.JuiceNotFoundException;
import katianne.JuiceStock.exception.JuiceStockExceededException;
import katianne.JuiceStock.mapper.JuiceMapper;
import katianne.JuiceStock.repository.JuiceRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JuiceServiceTest {

    private static final long INVALID_Juice_ID = 1L;

    @Mock
    private JuiceRepository JuiceRepository;

    private JuiceMapper JuiceMapper = JuiceMapper.INSTANCE;

    @InjectMocks
    private JuiceService JuiceService;

    @Test
    void whenJuiceInformedThenItShouldBeCreated() throws JuiceAlreadyRegisteredException {
        // given
        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        Juice expectedSavedJuice = JuiceMapper.toModel(expectedJuiceDTO);

        // when
        when(JuiceRepository.findByName(expectedJuiceDTO.getName())).thenReturn(Optional.empty());
        when(JuiceRepository.save(expectedSavedJuice)).thenReturn(expectedSavedJuice);

        //then
        JuiceDTO createdJuiceDTO = JuiceService.createJuice(expectedJuiceDTO);

        assertThat(createdJuiceDTO.getId(), is(equalTo(expectedJuiceDTO.getId())));
        assertThat(createdJuiceDTO.getName(), is(equalTo(expectedJuiceDTO.getName())));
        assertThat(createdJuiceDTO.getQuantity(), is(equalTo(expectedJuiceDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredJuiceInformedThenAnExceptionShouldBeThrown() {
        // given
        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        Juice duplicatedJuice = JuiceMapper.toModel(expectedJuiceDTO);

        // when
        when(JuiceRepository.findByName(expectedJuiceDTO.getName())).thenReturn(Optional.of(duplicatedJuice));

        // then
        assertThrows(JuiceAlreadyRegisteredException.class, () -> JuiceService.createJuice(expectedJuiceDTO));
    }

    @Test
    void whenValidJuiceNameIsGivenThenReturnAJuice() throws JuiceNotFoundException {
        // given
        JuiceDTO expectedFoundJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
         Juice expectedFoundJuice = JuiceMapper.toModel(expectedFoundJuiceDTO);

        // when
        when(JuiceRepository.findByName(expectedFoundJuice.getName())).thenReturn(Optional.of(expectedFoundJuice));

        // then
       JuiceDTO foundJuiceDTO = JuiceService.findByName(expectedFoundJuiceDTO.getName());

        assertThat(foundJuiceDTO, is(equalTo(expectedFoundJuiceDTO)));
    }

    @Test
    void whenNotRegisteredJuiceNameIsGivenThenThrowAnException() {
        // given
        JuiceDTO expectedFoundJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();

        // when
        when(JuiceRepository.findByName(expectedFoundJuiceDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(JuiceNotFoundException.class, () -> JuiceService.findByName(expectedFoundJuiceDTO.getName()));
    }

    @Test
    void whenListJuiceIsCalledThenReturnAListOfJuices() {
        // given
        JuiceDTO expectedFoundJuiceDTO = Juice.builder().build().toJuiceDTO();
        Juice expectedFoundJuice = JuiceMapper.toModel(expectedFoundJuiceDTO);

        //when
        when(JuiceRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundJuice));

        //then
        List<JuiceDTO> foundListJuicesDTO = JuiceService.listAll();

        assertThat(foundListJuicesDTO, is(not(empty())));
        assertThat(foundListJuicesDTO.get(0), is(equalTo(expectedFoundJuiceDTO)));
    }

    @Test
    void whenListJuiceIsCalledThenReturnAnEmptyListOfJuices() {
        //when
        when(JuiceRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<JuiceDTO> foundListJuicesDTO = JuiceService.listAll();

        assertThat(foundListJuicesDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenAJuiceShouldBeDeleted() throws JuiceNotFoundException{
        // given
        JuiceDTO expectedDeletedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        Juice expectedDeletedJuice = JuiceMapper.toModel(expectedDeletedJuiceDTO);

        // when
        when(JuiceRepository.findById(expectedDeletedJuiceDTO.getId())).thenReturn(Optional.of(expectedDeletedJuice));
        doNothing().when(JuiceRepository).deleteById(expectedDeletedJuiceDTO.getId());

        // then
        JuiceService.deleteById(expectedDeletedJuiceDTO.getId());

        verify(JuiceRepository, times(1)).findById(expectedDeletedJuiceDTO.getId());
        verify(JuiceRepository, times(1)).deleteById(expectedDeletedJuiceDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementJuiceStock() throws JuiceNotFoundException, JuiceStockExceededException {
        //given
        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        Juice expectedJuice = JuiceMapper.toModel(expectedJuiceDTO);

        //when
        when(JuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedJuice));
        when(JuiceRepository.save(expectedJuice)).thenReturn(expectedJuice);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedJuiceDTO.getQuantity() + quantityToIncrement;

        // then
        JuiceDTO incrementedJuiceDTO = JuiceService.increment(expectedJuiceDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedJuiceDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedJuiceDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        Juice expectedJuice= JuiceMapper.toModel(expectedJuiceDTO);

        when(JuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedvJuice));

        int quantityToIncrement = 80;
        assertThrows(JuiceStockExceededException.class, () -> JuiceService.increment(expectedJuiceDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        JuiceexpectedJuice = JuiceMapper.toModel(expectedJuiceDTO);

        when(JuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedJuice));

        int quantityToIncrement = 45;
        assertThrows(JuiceStockExceededException.class, () -> JuiceService.increment(expectedJuiceDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(JuiceRepository.findById(INVALID_Juice_ID)).thenReturn(Optional.empty());

        assertThrows(JuiceNotFoundException.class, () -> JuiceService.increment(INVALID_Juice_ID, quantityToIncrement));
    }
//
//    @Test
//    void whenDecrementIsCalledThenDecrementJuiceStock() throws JuiceNotFoundException, JuiceStockExceededException {
//        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//       Juice expectedJuice =JuiceMapper.toModel(expectedJuiceDTO);
//
//        when(JuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedJuice));
//        when(JuicerRepository.save(expectedJuicer)).thenReturn(expectedJuice);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedJuiceDTO.getQuantity() - quantityToDecrement;
//        JuiceDTO incrementedJuiceDTO = JuiceService.decrement(expectedJuiceDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedJuiceDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyJuiceStock() throws JuiceNotFoundException, JuiceStockExceededException {
//        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//        Juice expectedJuice = JuiceMapper.toModel(expectedJuiceDTO);
//
//        when(JuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedJuice));
//        when(JuiceRepository.save(expectedJuice)).thenReturn(expectedJuice);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedJuiceDTO.getQuantity() - quantityToDecrement;
//        JuiceDTO incrementedJuiceDTO = JuiceService.decrement(expectedJuiceDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedJuiceDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        JuiceDTO expectedJuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//       Juice expectedJuice =JuiceMapper.toModel(expectedJuiceDTO);
//
//        whenJuiceRepository.findById(expectedJuiceDTO.getId())).thenReturn(Optional.of(expectedJuice));
//
//        int quantityToDecrement = 80;
//        assertThrowsJuiceStockExceededException.class, () -> Juiceervice.decrement(expectedJuiceDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(JuiceRepository.findById(INVALID_Juice_ID)).thenReturn(Optional.empty());
//
//        assertThrows(JuiceNotFoundException.class, () -> Juiceervice.decrement(INVALID_Juice_ID, quantityToDecrement));
//    }
}
