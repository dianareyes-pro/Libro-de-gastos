package com.gestion.gastos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.UsuarioRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Categoria> listar(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    public Categoria crear(Categoria categoria, Long usuarioId) {
        if (categoriaRepository.findByUsuarioIdAndNombre(usuarioId, categoria.getNombre()).isPresent()) {
            return null; // ya existe una categoria con ese nombre para este usuario
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        categoria.setUsuario(usuario);
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria datos, Long usuarioId) {
        return categoriaRepository.findById(id)
                .filter(c -> c.getUsuario().getId().equals(usuarioId))
                .map(categoria -> {
                    categoria.setNombre(datos.getNombre());
                    return categoriaRepository.save(categoria);
                })
                .orElse(null);
    }

    public boolean eliminar(Long id, Long usuarioId) {
        return categoriaRepository.findById(id)
                .filter(c -> c.getUsuario().getId().equals(usuarioId))
                .map(c -> {
                    categoriaRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
