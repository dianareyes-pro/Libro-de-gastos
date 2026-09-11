package com.gestion.gastos;

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
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.IngresoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping
    public List<Ingreso> listar(HttpServletRequest request) {
        return ingresoRepository.findByUsuarioId(usuarioId(request));
    }

    @PostMapping
    public ResponseEntity<Ingreso> crear(@RequestBody Ingreso ingreso, HttpServletRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId(request)).orElseThrow();
        ingreso.setUsuario(usuario);
        Ingreso guardado = ingresoRepository.save(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingreso> actualizar(@PathVariable Long id, @RequestBody Ingreso datos, HttpServletRequest request) {
        return ingresoRepository.findById(id)
                .filter(i -> i.getUsuario().getId().equals(usuarioId(request)))
                .map(ingreso -> {
                    ingreso.setDescripcion(datos.getDescripcion());
                    ingreso.setMonto(datos.getMonto());
                    return ResponseEntity.ok(ingresoRepository.save(ingreso));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        return ingresoRepository.findById(id)
                .filter(i -> i.getUsuario().getId().equals(usuarioId(request)))
                .map(i -> {
                    ingresoRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}