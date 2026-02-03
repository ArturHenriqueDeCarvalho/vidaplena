package com.example.vidaplena.domain.entity.base;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Classe base para entidades com soft delete.
 * 
 * <p>
 * Utiliza anotações do Hibernate para implementar soft delete automático:
 * </p>
 * <ul>
 * <li><b>@SQLDelete:</b> Intercepta DELETE e executa UPDATE ao invés de
 * remover</li>
 * <li><b>@Where:</b> Adiciona filtro automático em todas as queries (deleted =
 * false)</li>
 * <li><b>@FilterDef:</b> Permite desabilitar filtro quando necessário buscar
 * deletados</li>
 * </ul>
 * 
 * <p>
 * <b>Benefícios do DRY:</b>
 * </p>
 * <ul>
 * <li>Elimina código duplicado de soft delete em cada Service</li>
 * <li>Impossível esquecer de filtrar registros deletados</li>
 * <li>Auditoria completa (quem deletou, quando)</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 2.0
 */
@MappedSuperclass
@SQLDelete(sql = "UPDATE {h-schema}{h-table} SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE {h-id} = ?")
@Where(clause = "deleted = false")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
@Data
public abstract class BaseEntity {

    /**
     * Indica se o registro foi deletado logicamente.
     * false = ativo, true = deletado.
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * Data e hora em que o registro foi deletado.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Usuário que deletou o registro.
     */
    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    /**
     * Método para soft delete manual (se necessário).
     * 
     * @param deletedBy Identificador do usuário que está deletando
     */
    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Método para restaurar um registro deletado.
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    /**
     * Verifica se o registro está deletado.
     * 
     * @return true se deletado, false caso contrário
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }
}
