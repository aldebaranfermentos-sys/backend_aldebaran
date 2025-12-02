package com.app.inventario.repository;

import com.app.inventario.entities.EquipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<EquipoEntity, Long> {
    List<EquipoEntity> findByEstado(String estado);
}