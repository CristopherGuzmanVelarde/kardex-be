package pe.edu.vallegrande.kardex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.kardex.model.Inventario;
import pe.edu.vallegrande.kardex.model.Producto;
import pe.edu.vallegrande.kardex.service.InventarioService;
import pe.edu.vallegrande.kardex.service.ProductoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    /**
     * Genera un reporte de movimientos de inventario por rango de fechas
     */
    @GetMapping("/movimientos")
    public Flux<Inventario> reporteMovimientosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return inventarioService.findByFechaBetween(fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());
    }

    /**
     * Genera un reporte de stock actual de productos
     */
    @GetMapping("/stock")
    public Flux<Producto> reporteStockActual() {
        return productoService.findAll();
    }

    /**
     * Genera un reporte de productos con stock bajo (menor al mínimo establecido)
     */
    @GetMapping("/stock-bajo")
    public Flux<Producto> reporteStockBajo() {
        return productoService.findStockBajo();
    }

    /**
     * Genera un reporte de valorización de inventario
     */
    @GetMapping("/valorizacion")
    public Mono<Map<String, Object>> reporteValorizacion() {
        return productoService.getValorizacionTotal();
    }
}