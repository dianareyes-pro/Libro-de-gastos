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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GastoController {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping("/")
    public String inicio() {
        return "¡Bienvenido al sistema de Control de Gastos Personales! 🚀";
    }

    @GetMapping("/api/gastos")
    public List<Gasto> listar(@RequestParam(required = false) Long categoriaId, HttpServletRequest request) {
        Long uid = usuarioId(request);
        if (categoriaId != null) {
            return gastoRepository.findByUsuarioIdAndCategoriaId(uid, categoriaId);
        }
        return gastoRepository.findByUsuarioId(uid);
    }

    @PostMapping("/api/gastos")
    public ResponseEntity<Gasto> crear(@RequestBody Gasto gasto, HttpServletRequest request) {
        Long uid = usuarioId(request);
        if (gasto.getCategoria() != null && gasto.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(gasto.getCategoria().getId()).orElse(null);
            if (categoria == null || !categoria.getUsuario().getId().equals(uid)) {
                return ResponseEntity.badRequest().build();
            }
            gasto.setCategoria(categoria);
        }
        Usuario usuario = usuarioRepository.findById(uid).orElseThrow();
        gasto.setUsuario(usuario);
        Gasto guardado = gastoRepository.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/api/gastos/{id}")
    public ResponseEntity<Gasto> actualizar(@PathVariable Long id, @RequestBody Gasto datos, HttpServletRequest request) {
        Long uid = usuarioId(request);
        return gastoRepository.findById(id)
                .filter(g -> g.getUsuario().getId().equals(uid))
                .map(gasto -> {
                    gasto.setDescripcion(datos.getDescripcion());
                    gasto.setMonto(datos.getMonto());
                    if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
                        categoriaRepository.findById(datos.getCategoria().getId())
                                .filter(c -> c.getUsuario().getId().equals(uid))
                                .ifPresent(gasto::setCategoria);
                    }
                    return ResponseEntity.ok(gastoRepository.save(gasto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/gastos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        return gastoRepository.findById(id)
                .filter(g -> g.getUsuario().getId().equals(usuarioId(request)))
                .map(g -> {
                    gastoRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}