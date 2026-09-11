package com.gestion.gastos.controller;

import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> datos) {
        UsuarioService.ResultadoRegistro resultado = usuarioService.registrar(
                datos.get("username"), datos.get("password"), datos.get("nombre"));

        if (!resultado.exito) {
            HttpStatus status = "Ese usuario ya existe.".equals(resultado.error)
                    ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", resultado.error));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Usuario creado correctamente."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos, HttpServletRequest request) {
        Usuario usuario = usuarioService.autenticar(datos.get("username"), datos.get("password"));
        if (usuario == null) {
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
