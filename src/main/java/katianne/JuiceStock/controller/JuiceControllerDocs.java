package katianne.JuiceStock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import katianne.JuiceStock.dto.JuiceDTO;
import katianne.JuiceStock.dto.QuantityDTO;
import katianne.JuiceStock.exception.JuiceAlreadyRegisteredException;
import katianne.JuiceStock.exception.JuiceNotFoundException;
import katianne.JuiceStock.exception.JuiceStockExceededException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manages Juice stock")
public interface JuiceControllerDocs {

    @ApiOperation(value = "Juice creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success Juice creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    JuiceDTO createJuice(JuiceDTO JuiceDTO) throws JuiceAlreadyRegisteredException;

    @ApiOperation(value = "Returns Juice found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success Juice found in the system"),
            @ApiResponse(code = 404, message = "Juice with given name not found.")
    })
    JuiceDTO findByName(@PathVariable String name) throws JuiceNotFoundException;

    @ApiOperation(value = "Returns a list of all juices registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all juices registered in the system"),
    })
    List<JuiceDTO> listJuices();

    @ApiOperation(value = "Delete a juice found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success juice deleted in the system"),
            @ApiResponse(code = 404, message = "Juice with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws JuiceNotFoundException;
}
