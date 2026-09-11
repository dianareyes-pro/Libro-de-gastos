package com.gestion.gastos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.gastos.model.Ingreso;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.IngresoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

@Service
public class IngresoService {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Ingreso> listar(Long usuarioId) {
        return ingresoRepository.findByUsuarioId(usuarioId);
    }

    public Ingreso crear(Ingreso ingreso, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        ingreso.setUsuario(usuario);
        return ingresoRepository.save(ingreso);
    }

    public Ingreso actualizar(Long id, Ingreso datos, Long usuarioId) {
        return ingresoRepository.findById(id)
                .filter(i -> i.getUsuario().getId().equals(usuarioId))
                .map(ingreso -> {
                    ingreso.setDescripcion(datos.getDescripcion());
                    ingreso.setMonto(datos.getMonto());
                    return ingresoRepository.save(ingreso);
                })
                .orElse(null);
    }

    public boolean eliminar(Long id, Long usuarioId) {
        return ingresoRepository.findById(id)
                .filter(i -> i.getUsuario().getId().equals(usuarioId))
                .map(i -> {
                    ingresoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
