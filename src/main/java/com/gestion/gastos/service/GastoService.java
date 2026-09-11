package com.gestion.gastos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Gasto> listar(Long usuarioId, Long categoriaId) {
        if (categoriaId != null) {
            return gastoRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId);
        }
        return gastoRepository.findByUsuarioId(usuarioId);
    }

    public Gasto crear(Gasto gasto, Long usuarioId) {
        if (gasto.getCategoria() != null && gasto.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(gasto.getCategoria().getId()).orElse(null);
            if (categoria == null || !categoria.getUsuario().getId().equals(usuarioId)) {
                return null; // categoria invalida o de otro usuario
            }
            gasto.setCategoria(categoria);
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        gasto.setUsuario(usuario);
        return gastoRepository.save(gasto);
    }

    public Gasto actualizar(Long id, Gasto datos, Long usuarioId) {
        return gastoRepository.findById(id)
                .filter(g -> g.getUsuario().getId().equals(usuarioId))
                .map(gasto -> {
                    gasto.setDescripcion(datos.getDescripcion());
                    gasto.setMonto(datos.getMonto());
                    if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
                        categoriaRepository.findById(datos.getCategoria().getId())
                                .filter(c -> c.getUsuario().getId().equals(usuarioId))
                                .ifPresent(gasto::setCategoria);
                    }
                    return gastoRepository.save(gasto);
                })
                .orElse(null);
    }

    public boolean eliminar(Long id, Long usuarioId) {
        return gastoRepository.findById(id)
                .filter(g -> g.getUsuario().getId().equals(usuarioId))
                .map(g -> {
                    gastoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
