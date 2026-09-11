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

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping
    public List<Categoria> listar(HttpServletRequest request) {
        return categoriaRepository.findByUsuarioId(usuarioId(request));
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria, HttpServletRequest request) {
        Long uid = usuarioId(request);
        if (categoriaRepository.findByUsuarioIdAndNombre(uid, categoria.getNombre()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Usuario usuario = usuarioRepository.findById(uid).orElseThrow();
        categoria.setUsuario(usuario);
        Categoria guardada = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria datos, HttpServletRequest request) {
        return categoriaRepository.findById(id)
                .filter(c -> c.getUsuario().getId().equals(usuarioId(request)))
                .map(categoria -> {
                    categoria.setNombre(datos.getNombre());
                    return ResponseEntity.ok(categoriaRepository.save(categoria));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        return categoriaRepository.findById(id)
                .filter(c -> c.getUsuario().getId().equals(usuarioId(request)))
                .map(c -> {
                    categoriaRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}