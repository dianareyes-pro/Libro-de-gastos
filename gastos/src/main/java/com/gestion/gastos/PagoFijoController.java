package com.gestion.gastos;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
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

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.model.PagoFijo;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.PagoFijoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pagos-fijos")
public class PagoFijoController {

    @Autowired
    private PagoFijoRepository pagoFijoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long usuarioId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("usuarioId");
    }

    @GetMapping
    public List<Map<String, Object>> listar(HttpServletRequest request) {
        Long uid = usuarioId(request);
        YearMonth mesActual = YearMonth.now();
        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();

        return pagoFijoRepository.findByUsuarioIdAndActivoTrue(uid).stream().map(pf -> {
            boolean pagado = !gastoRepository
                    .findByPagoFijoIdAndFechaBetween(pf.getId(), inicio, fin)
                    .isEmpty();
            Map<String, Object> item = new HashMap<>();
            item.put("id", pf.getId());
            item.put("descripcion", pf.getDescripcion());
            item.put("monto", pf.getMonto());
            item.put("diaPago", pf.getDiaPago());
            item.put("categoria", pf.getCategoria());
            item.put("pagadoEsteMes", pagado);
            return item;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<PagoFijo> crear(@RequestBody PagoFijo pagoFijo, HttpServletRequest request) {
        Long uid = usuarioId(request);
        if (pagoFijo.getCategoria() != null && pagoFijo.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(pagoFijo.getCategoria().getId()).orElse(null);
            if (categoria == null || !categoria.getUsuario().getId().equals(uid)) return ResponseEntity.badRequest().build();
            pagoFijo.setCategoria(categoria);
        }
        Usuario usuario = usuarioRepository.findById(uid).orElseThrow();
        pagoFijo.setUsuario(usuario);
        PagoFijo guardado = pagoFijoRepository.save(pagoFijo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoFijo> actualizar(@PathVariable Long id, @RequestBody PagoFijo datos, HttpServletRequest request) {
        Long uid = usuarioId(request);
        return pagoFijoRepository.findById(id)
                .filter(pf -> pf.getUsuario().getId().equals(uid))
                .map(pagoFijo -> {
                    pagoFijo.setDescripcion(datos.getDescripcion());
                    pagoFijo.setMonto(datos.getMonto());
                    pagoFijo.setDiaPago(datos.getDiaPago());
                    if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
                        categoriaRepository.findById(datos.getCategoria().getId())
                                .filter(c -> c.getUsuario().getId().equals(uid))
                                .ifPresent(pagoFijo::setCategoria);
                    } else {
                        pagoFijo.setCategoria(null);
                    }
                    return ResponseEntity.ok(pagoFijoRepository.save(pagoFijo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Gasto> pagar(@PathVariable Long id, HttpServletRequest request) {
        Long uid = usuarioId(request);
        PagoFijo pagoFijo = pagoFijoRepository.findById(id).orElse(null);
        if (pagoFijo == null || !pagoFijo.getUsuario().getId().equals(uid)) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioRepository.findById(uid).orElseThrow();
        Gasto gasto = new Gasto();
        gasto.setDescripcion(pagoFijo.getDescripcion());
        gasto.setMonto(pagoFijo.getMonto());
        gasto.setCategoria(pagoFijo.getCategoria());
        gasto.setPagoFijo(pagoFijo);
        gasto.setUsuario(usuario);
        Gasto guardado = gastoRepository.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        return pagoFijoRepository.findById(id)
                .filter(pf -> pf.getUsuario().getId().equals(usuarioId(request)))
                .map(pf -> {
                    pagoFijoRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}