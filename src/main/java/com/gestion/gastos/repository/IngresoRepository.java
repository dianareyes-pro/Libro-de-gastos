package com.gestion.gastos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.gastos.model.Ingreso;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.usuario.id = :usuarioId")
    BigDecimal sumarTotal(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.usuario.id = :usuarioId AND YEAR(i.fecha) = :anio AND MONTH(i.fecha) = :mes")
    BigDecimal sumarPorMes(@Param("usuarioId") Long usuarioId, @Param("anio") int anio, @Param("mes") int mes);
}