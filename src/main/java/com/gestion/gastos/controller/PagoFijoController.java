package com.gestion.gastos.controller;

import java.util.List;
import java.util.Map;

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

import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.model.PagoFijo;
import com.gestion.gastos.service.PagoFijoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pagos-fijos")
public class PagoFijoController {

    @Autowired
    private PagoFijoService pagoFijoService;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping
    public List<Map<String, Object>> listar(HttpServletRequest request) {
        return pagoFijoService.listar(usuarioId(request));
    }

    @PostMapping
    public ResponseEntity<PagoFijo> crear(@RequestBody PagoFijo pagoFijo, HttpServletRequest request) {
        PagoFijo guardado = pagoFijoService.crear(pagoFijo, usuarioId(request));
        if (guardado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoFijo> actualizar(@PathVariable Long id, @RequestBody PagoFijo datos, HttpServletRequest request) {
        PagoFijo actualizado = pagoFijoService.actualizar(id, datos, usuarioId(request));
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Gasto> pagar(@PathVariable Long id, HttpServletRequest request) {
        Gasto gasto = pagoFijoService.pagar(id, usuarioId(request));
        return gasto != null ? ResponseEntity.status(HttpStatus.CREATED).body(gasto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        boolean eliminado = pagoFijoService.eliminar(id, usuarioId(request));
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
