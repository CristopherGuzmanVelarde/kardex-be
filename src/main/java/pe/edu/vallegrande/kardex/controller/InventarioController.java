package pe.edu.vallegrande.kardex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.kardex.model.Inventario;
import pe.edu.vallegrande.kardex.service.InventarioService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService inventarioService;
    
    @GetMapping
    public Flux<Inventario> getAllMovimientos() {
        return inventarioService.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<Inventario> getMovimientoById(@PathVariable String id) {
        return inventarioService.findById(id);
    }
    
    @GetMapping("/producto/{skuProducto}")
    public Flux<Inventario> getMovimientosByProducto(@PathVariable String skuProducto) {
        return inventarioService.findBySkuProducto(skuProducto);
    }
    
    @GetMapping("/tipo/{tipoTransaccion}")
    public Flux<Inventario> getMovimientosByTipo(@PathVariable Inventario.TipoTransaccion tipoTransaccion) {
        return inventarioService.findByTipoTransaccion(tipoTransaccion);
    }
    
    @GetMapping("/periodo")
    public Flux<Inventario> getMovimientosByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return inventarioService.findByFechaTransaccionBetween(inicio, fin);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Inventario> createMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }
    
    @PutMapping("/{id}")
    public Mono<Inventario> updateMovimiento(@PathVariable String id, @RequestBody Inventario inventario) {
        return inventarioService.update(id, inventario);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Inventario> deleteMovimiento(@PathVariable String id) {
        return inventarioService.delete(id);
    }
    
    // Endpoint para SSE (Server-Sent Events) - Streaming de movimientos
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Inventario> streamAllMovimientos() {
        return inventarioService.findAll()
                .delayElements(Duration.ofMillis(100));
    }
    
    // Endpoints para reportes
    @GetMapping("/reporte/periodo")
    public Flux<Inventario> reporteMovimientosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return inventarioService.generarReporteMovimientosPorPeriodo(inicio, fin);
    }
    
    @GetMapping("/reporte/producto/{skuProducto}")
    public Flux<Inventario> reporteMovimientosPorProducto(@PathVariable String skuProducto) {
        return inventarioService.generarReporteMovimientosPorProducto(skuProducto);
    }
}