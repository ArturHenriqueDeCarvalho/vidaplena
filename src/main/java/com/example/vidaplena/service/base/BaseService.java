package com.example.vidaplena.service.base;

import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.mapper.EntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service base genérico com operações CRUD comuns.
 * 
 * <p>
 * Implementa o padrão DRY (Don't Repeat Yourself) fornecendo funcionalidades
 * CRUD reutilizáveis para todas as entidades do sistema.
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
public abstract class BaseService<T, ID, REQ, RES> {

    /**
     * Retorna o repositório JPA da entidade.
     * 
     * @return Repositório JPA
     */
    protected abstract JpaRepository<T, ID> getRepository();

    /**
     * Retorna o mapper para conversão Entidade ↔ DTO.
     * 
     * @return Mapper da entidade
     */
    protected abstract EntityMapper<T, REQ, RES> getMapper();

    /**
     * Retorna o nome da entidade para mensagens de erro.
     * 
     * @return Nome da entidade (ex: "Usuário", "Atendimento")
     */
    protected abstract String getEntityName();

    /**
     * Busca uma entidade pelo ID.
     * 
     * @param id ID da entidade
     * @return Entidade encontrada
     * @throws ResourceNotFoundException se não encontrada
     */
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), "ID", id));
    }

    /**
     * Busca uma entidade pelo ID e retorna como DTO.
     * 
     * @param id ID da entidade
     * @return DTO de response
     * @throws ResourceNotFoundException se não encontrada
     */
    @Transactional(readOnly = true)
    public RES findByIdAsResponse(ID id) {
        T entity = findById(id);
        return getMapper().toResponse(entity);
    }

    /**
     * Retorna todas as entidades como DTOs.
     * 
     * @return Lista de DTOs
     */
    @Transactional(readOnly = true)
    public List<RES> findAll() {
        return getRepository().findAll().stream()
                .map(getMapper()::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma nova entidade.
     * 
     * @param request DTO de request
     * @return DTO da entidade criada
     */
    @Transactional
    public RES create(REQ request) {
        T entity = getMapper().toEntity(request);
        T savedEntity = getRepository().save(entity);
        return getMapper().toResponse(savedEntity);
    }

    /**
     * Atualiza uma entidade existente.
     * 
     * @param id      ID da entidade
     * @param request DTO com novos dados
     * @return DTO da entidade atualizada
     * @throws ResourceNotFoundException se não encontrada
     */
    @Transactional
    public RES update(ID id, REQ request) {
        T entity = findById(id);
        getMapper().updateEntity(entity, request);
        T updatedEntity = getRepository().save(entity);
        return getMapper().toResponse(updatedEntity);
    }

    /**
     * Remove uma entidade pelo ID.
     * 
     * @param id ID da entidade
     * @throws ResourceNotFoundException se não encontrada
     */
    @Transactional
    public void delete(ID id) {
        T entity = findById(id);
        getRepository().delete(entity);
    }
}
