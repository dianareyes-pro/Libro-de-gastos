package com.gestion.gastos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {

    @GetMapping("/api/saludo")
    public String saludo() {
        return "Sistema de Control de Gastos funcionando correctamente";
    }
}
