package com.example.vidaplena.controller.base;

import com.example.vidaplena.service.base.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller base genérico com endpoints REST padrão.
 * 
 * <p>
 * Implementa o padrão DRY fornecendo endpoints CRUD reutilizáveis
 * para todas as entidades do sistema.
 * </p>
 * 
 * @param <T>   Tipo da entidade
 * @param <ID>  Tipo do identificador
 * @param <REQ> Tipo do DTO de request
 * @param <RES> Tipo do DTO de response
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public abstract class BaseController<T, ID, REQ, RES> {

    /**
     * Retorna o service da entidade.
     * 
     * @return Service base
     */
    protected abstract BaseService<T, ID, REQ, RES> getService();

    /**
     * Lista todas as entidades.
     * 
     * @return Lista de DTOs
     */
    @GetMapping
    @Operation(summary = "Listar todos", description = "Retorna lista de todos os registros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<RES>> findAll() {
        List<RES> results = getService().findAll();
        return ResponseEntity.ok(results);
    }

    /**
     * Busca uma entidade pelo ID.
     * 
     * @param id ID da entidade
     * @return DTO da entidade
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna um registro específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<RES> findById(@PathVariable ID id) {
        RES response = getService().findByIdAsResponse(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma nova entidade.
     * 
     * @param request DTO de request
     * @return DTO da entidade criada
     */
    @PostMapping
    @Operation(summary = "Criar novo", description = "Cria um novo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<RES> create(@RequestBody @Valid REQ request) {
        RES response = getService().create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza uma entidade existente.
     * 
     * @param id      ID da entidade
     * @param request DTO com novos dados
     * @return DTO da entidade atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar", description = "Atualiza um registro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<RES> update(@PathVariable ID id, @RequestBody @Valid REQ request) {
        RES response = getService().update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove uma entidade pelo ID.
     * 
     * @param id ID da entidade
     * @return Resposta vazia
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover", description = "Remove um registro pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}
