package com.gestion.gastos.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.gastos.model.Categoria;
import com.gestion.gastos.model.Gasto;
import com.gestion.gastos.model.PagoFijo;
import com.gestion.gastos.model.Usuario;
import com.gestion.gastos.repository.CategoriaRepository;
import com.gestion.gastos.repository.GastoRepository;
import com.gestion.gastos.repository.PagoFijoRepository;
import com.gestion.gastos.repository.UsuarioRepository;

@Service
public class PagoFijoService {

    @Autowired
    private PagoFijoRepository pagoFijoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Map<String, Object>> listar(Long usuarioId) {
        YearMonth mesActual = YearMonth.now();
        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();

        return pagoFijoRepository.findByUsuarioIdAndActivoTrue(usuarioId).stream().map(pf -> {
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

    public PagoFijo crear(PagoFijo pagoFijo, Long usuarioId) {
        if (pagoFijo.getCategoria() != null && pagoFijo.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(pagoFijo.getCategoria().getId()).orElse(null);
            if (categoria == null || !categoria.getUsuario().getId().equals(usuarioId)) {
                return null;
            }
            pagoFijo.setCategoria(categoria);
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        pagoFijo.setUsuario(usuario);
        return pagoFijoRepository.save(pagoFijo);
    }

    public PagoFijo actualizar(Long id, PagoFijo datos, Long usuarioId) {
        return pagoFijoRepository.findById(id)
                .filter(pf -> pf.getUsuario().getId().equals(usuarioId))
                .map(pagoFijo -> {
                    pagoFijo.setDescripcion(datos.getDescripcion());
                    pagoFijo.setMonto(datos.getMonto());
                    pagoFijo.setDiaPago(datos.getDiaPago());
                    if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
                        categoriaRepository.findById(datos.getCategoria().getId())
                                .filter(c -> c.getUsuario().getId().equals(usuarioId))
                                .ifPresent(pagoFijo::setCategoria);
                    } else {
                        pagoFijo.setCategoria(null);
                    }
                    return pagoFijoRepository.save(pagoFijo);
                })
                .orElse(null);
    }

    public Gasto pagar(Long id, Long usuarioId) {
        PagoFijo pagoFijo = pagoFijoRepository.findById(id).orElse(null);
        if (pagoFijo == null || !pagoFijo.getUsuario().getId().equals(usuarioId)) {
            return null;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Gasto gasto = new Gasto();
        gasto.setDescripcion(pagoFijo.getDescripcion());
        gasto.setMonto(pagoFijo.getMonto());
        gasto.setCategoria(pagoFijo.getCategoria());
        gasto.setPagoFijo(pagoFijo);
        gasto.setUsuario(usuario);
        return gastoRepository.save(gasto);
    }

    public boolean eliminar(Long id, Long usuarioId) {
        return pagoFijoRepository.findById(id)
                .filter(pf -> pf.getUsuario().getId().equals(usuarioId))
                .map(pf -> {
                    pagoFijoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
