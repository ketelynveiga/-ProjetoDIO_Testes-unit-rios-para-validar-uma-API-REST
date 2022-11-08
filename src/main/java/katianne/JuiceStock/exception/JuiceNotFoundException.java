package katianne.JuiceStock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JuiceNotFoundException extends Exception {

    public JuiceNotFoundException(String JuiceName) {
        super(String.format("Juice with name %s not found in the system.", JuiceName));
    }

    public JuiceNotFoundException(Long id) {
        super(String.format("Juice with id %s not found in the system.", id));
    }
}
