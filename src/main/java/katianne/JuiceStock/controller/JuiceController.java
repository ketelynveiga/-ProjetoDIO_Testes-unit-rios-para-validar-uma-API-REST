package katianne.JuiceStock.controller;

import lombok.AllArgsConstructor;
import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.dto.QuantityDTO;
import katianne.JuiceStock.exception.JuiceAlreadyRegisteredException;
import katianne.JuiceStock.exception.JuiceNotFoundException;
import katianne.JuiceStock.exception.JuiceStockExceededException;
import katianne.JuiceStock.service.JuiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/v1/Juices")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JuiceController implements JuiceControllerDocs {

    private final JuiceService JuiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JuiceDTO createJuice(@RequestBody @Valid JuiceDTO JuiceDTO) throws JuiceAlreadyRegisteredException {
        return JuiceService.createJuice(JuiceDTO);
    }

    @GetMapping("/{name}")
    public JuiceDTO findByName(@PathVariable String name) throws JuicerNotFoundException {
        return JuiceService.findByName(name);
    }

    @GetMapping
    public List<JuiceDTO> listJuice() {
        return JuiceService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws JuiceNotFoundException {
        JuiceService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public JuiceDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws JuiceNotFoundException, JuiceStockExceededException {
        return JuiceService.increment(id, quantityDTO.getQuantity());
    }
}
