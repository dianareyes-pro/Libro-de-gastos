package com.gestion.gastos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static class ResultadoRegistro {
        public boolean exito;
        public String error;
    }

    public ResultadoRegistro registrar(String username, String password, String nombre) {
        ResultadoRegistro resultado = new ResultadoRegistro();

        if (username == null || password == null || nombre == null ||
                username.isBlank() || password.isBlank() || nombre.isBlank()) {
            resultado.exito = false;
            resultado.error = "Todos los campos son obligatorios.";
            return resultado;
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            resultado.exito = false;
            resultado.error = "Ese usuario ya existe.";
            return resultado;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(encoder.encode(password));
        usuario.setNombre(nombre);
        usuarioRepository.save(usuario);

        resultado.exito = true;
        return resultado;
    }

    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null || !encoder.matches(password, usuario.getPassword())) {
            return null;
        }
        return usuario;
    }
}
