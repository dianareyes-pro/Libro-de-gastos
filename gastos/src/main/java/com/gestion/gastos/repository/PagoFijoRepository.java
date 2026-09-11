package com.gestion.gastos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.gastos.model.PagoFijo;

public interface PagoFijoRepository extends JpaRepository<PagoFijo, Long> {
    List<PagoFijo> findByUsuarioIdAndActivoTrue(Long usuarioId);
}