package com.gestion.gastos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.IngresoRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping("/general")
    public Map<String, Object> general(HttpServletRequest request) {
        Long uid = usuarioId(request);
        BigDecimal ingresos = ingresoRepository.sumarTotal(uid);
        BigDecimal gastos = gastoRepository.sumarTotal(uid);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ingresos", ingresos);
        resultado.put("gastos", gastos);
        resultado.put("balance", ingresos.subtract(gastos));
        return resultado;
    }

    @GetMapping("/mensual")
    public Map<String, Object> mensual(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            HttpServletRequest request) {
        Long uid = usuarioId(request);
        LocalDate hoy = LocalDate.now();
        int a = (anio != null) ? anio : hoy.getYear();
        int m = (mes != null) ? mes : hoy.getMonthValue();

        BigDecimal ingresos = ingresoRepository.sumarPorMes(uid, a, m);
        BigDecimal gastos = gastoRepository.sumarPorMes(uid, a, m);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("anio", a);
        resultado.put("mes", m);
        resultado.put("ingresos", ingresos);
        resultado.put("gastos", gastos);
        resultado.put("balance", ingresos.subtract(gastos));
        return resultado;
    }
}