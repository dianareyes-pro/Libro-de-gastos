package com.gestion.gastos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.IngresoRepository;

@Service
public class BalanceService {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    public Map<String, Object> general(Long usuarioId) {
        BigDecimal ingresos = ingresoRepository.sumarTotal(usuarioId);
        BigDecimal gastos = gastoRepository.sumarTotal(usuarioId);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ingresos", ingresos);
        resultado.put("gastos", gastos);
        resultado.put("balance", ingresos.subtract(gastos));
        return resultado;
    }

    public Map<String, Object> mensual(Integer anio, Integer mes, Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        int a = (anio != null) ? anio : hoy.getYear();
        int m = (mes != null) ? mes : hoy.getMonthValue();

        BigDecimal ingresos = ingresoRepository.sumarPorMes(usuarioId, a, m);
        BigDecimal gastos = gastoRepository.sumarPorMes(usuarioId, a, m);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("anio", a);
        resultado.put("mes", m);
        resultado.put("ingresos", ingresos);
        resultado.put("gastos", gastos);
        resultado.put("balance", ingresos.subtract(gastos));
        return resultado;
    }
}
