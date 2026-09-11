package com.gestion.gastos.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.gastos.model.Gasto;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByUsuarioId(Long usuarioId);

    List<Gasto> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);

    List<Gasto> findByPagoFijoIdAndFechaBetween(Long pagoFijoId, LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId")
    BigDecimal sumarTotal(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId AND YEAR(g.fecha) = :anio AND MONTH(g.fecha) = :mes")
    BigDecimal sumarPorMes(@Param("usuarioId") Long usuarioId, @Param("anio") int anio, @Param("mes") int mes);
}