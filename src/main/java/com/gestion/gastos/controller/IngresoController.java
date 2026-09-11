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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.gastos.model.Ingreso;
import com.gestion.gastos.service.IngresoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    @Autowired
    private IngresoService ingresoService;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping
    public List<Ingreso> listar(HttpServletRequest request) {
        return ingresoService.listar(usuarioId(request));
    }

    @PostMapping
    public ResponseEntity<Ingreso> crear(@RequestBody Ingreso ingreso, HttpServletRequest request) {
        Ingreso guardado = ingresoService.crear(ingreso, usuarioId(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingreso> actualizar(@PathVariable Long id, @RequestBody Ingreso datos, HttpServletRequest request) {
        Ingreso actualizado = ingresoService.actualizar(id, datos, usuarioId(request));
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        boolean eliminado = ingresoService.eliminar(id, usuarioId(request));
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
