package katianne.JuiceStock.service;

import lombok.AllArgsConstructor;
import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.entity.Juice;
import katianne.JuiceStock.exception.JuiceAlreadyRegisteredException;
import katianne.JuiceStock.exception.JuiceNotFoundException;
import katianne.JuiceStock.exception.JuiceStockExceededException;
import katianne.JuiceStock.mapper.JuiceMapper;
import katianne.JuiceStock.repository.JuiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JuiceService {

    private final JuiceRepository JuiceRepository;
    private final JuiceMapper JuiceMapper = uiceMapper.INSTANCE;

    public JuiceDTO createJuice(JuiceDTO JuiceDTO) throws JuiceAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(JuiceDTO.getName());
        Juice Juice = JuiceMapper.toModel(JuiceDTO);
        Juice savedJuice = JuiceRepository.save(Juice);
        return JuiceMapper.toDTO(savedJuice);
    }

    public JuiceDTO findByName(String name) throws JuiceNotFoundException {
        Juice foundJuice = JuiceRepository.findByName(name)
                .orElseThrow(() -> new JuiceNotFoundException(name));
        return JuiceMapper.toDTO(foundJuice);
    }

    public List<JuiceDTO> listAll() {
        return JuiceRepository.findAll()
                .stream()
                .map(JuiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws JuiceNotFoundException {
        verifyIfExists(id);
        JuiceRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws JuiceAlreadyRegisteredException {
        Optional<Juice> optSavedJuice =JuiceRepository.findByName(name);
        if (optSavedJuice.isPresent()) {
            throw new JuiceAlreadyRegisteredException(name);
        }
    }

    private Juice verifyIfExists(Long id) throws JuiceNotFoundException {
        return JuiceRepository.findById(id)
                .orElseThrow(() -> new JuiceNotFoundException(id));
    }

    public JuiceDTO increment(Long id, int quantityToIncrement) throws JuiceNotFoundException, JuiceStockExceededException {
        Juice JuiceToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + JuiceToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= JuiceToIncrementStock.getMax()) {
            JuiceToIncrementStock.setQuantity(JuiceToIncrementStock.getQuantity() + quantityToIncrement);
            Juice incrementedJuiceStock = JuiceRepository.save(JuiceToIncrementStock);
            return JuiceMapper.toDTO(incrementedJuiceStock);
        }
        throw new JuiceStockExceededException(id, quantityToIncrement);
    }
}
