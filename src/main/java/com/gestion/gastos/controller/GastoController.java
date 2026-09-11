package com.gestion.gastos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.service.GastoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GastoController {

    @Autowired
    private GastoService gastoService;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping("/")
    public String inicio() {
        return "¡Bienvenido al sistema de Control de Gastos Personales! 🚀";
    }

    @GetMapping("/api/gastos")
    public List<Gasto> listar(@RequestParam(required = false) Long categoriaId, HttpServletRequest request) {
        return gastoService.listar(usuarioId(request), categoriaId);
    }

    @PostMapping("/api/gastos")
    public ResponseEntity<Gasto> crear(@RequestBody Gasto gasto, HttpServletRequest request) {
        Gasto guardado = gastoService.crear(gasto, usuarioId(request));
        if (guardado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/api/gastos/{id}")
    public ResponseEntity<Gasto> actualizar(@PathVariable Long id, @RequestBody Gasto datos, HttpServletRequest request) {
        Gasto actualizado = gastoService.actualizar(id, datos, usuarioId(request));
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/gastos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        boolean eliminado = gastoService.eliminar(id, usuarioId(request));
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
