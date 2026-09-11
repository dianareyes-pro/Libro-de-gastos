package com.gestion.gastos;

import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> datos) {
        String username = datos.get("username");
        String password = datos.get("password");
        String nombre = datos.get("nombre");

        if (username == null || password == null || nombre == null ||
            username.isBlank() || password.isBlank() || nombre.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios."));
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Ese usuario ya existe."));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(encoder.encode(password));
        usuario.setNombre(nombre);
        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Usuario creado correctamente."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos, HttpServletRequest request) {
        String username = datos.get("username");
        String password = datos.get("password");

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null || !encoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario o contraseña incorrectos."));
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombre());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", usuario.getId());
        respuesta.put("nombre", usuario.getNombre());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada."));
    }

    @GetMapping("/sesion")
    public ResponseEntity<?> sesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", session.getAttribute("usuarioId"));
        respuesta.put("nombre", session.getAttribute("usuarioNombre"));
        return ResponseEntity.ok(respuesta);
    }
}