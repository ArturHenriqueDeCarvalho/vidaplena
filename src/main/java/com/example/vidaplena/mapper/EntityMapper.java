package com.example.vidaplena.mapper;

/**
 * Interface genérica para mapeamento entre entidades e DTOs.
 * 
 * <p>
 * Define o contrato para conversão bidirecional entre:
 * </p>
 * <ul>
 * <li>Entidade (T) - Modelo de domínio</li>
 * <li>Request (REQ) - DTO de entrada</li>
 * <li>Response (RES) - DTO de saída</li>
 * </ul>
 * 
 * @param <T>   Tipo da entidade
 * @param <REQ> Tipo do DTO de request
 * @param <RES> Tipo do DTO de response
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public interface EntityMapper<T, REQ, RES> {

    /**
     * Converte um DTO de request para entidade.
     * 
     * @param request DTO de entrada
     * @return Entidade criada
     */
    T toEntity(REQ request);

    /**
     * Converte uma entidade para DTO de response.
     * 
     * @param entity Entidade do domínio
     * @return DTO de saída
     */
    RES toResponse(T entity);

    /**
     * Atualiza uma entidade existente com dados do request.
     * 
     * @param entity  Entidade a ser atualizada
     * @param request DTO com novos dados
     */
    void updateEntity(T entity, REQ request);
}
