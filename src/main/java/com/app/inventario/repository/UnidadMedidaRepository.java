package com.app.inventario.repository;

import com.app.inventario.entities.UnidadMedidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedidaEntity, Integer> {}
