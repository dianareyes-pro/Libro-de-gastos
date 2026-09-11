package com.gestion.gastos.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.gastos.service.BalanceService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping("/general")
    public Map<String, Object> general(HttpServletRequest request) {
        return balanceService.general(usuarioId(request));
    }

    @GetMapping("/mensual")
    public Map<String, Object> mensual(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            HttpServletRequest request) {
        return balanceService.mensual(anio, mes, usuarioId(request));
    }
}
