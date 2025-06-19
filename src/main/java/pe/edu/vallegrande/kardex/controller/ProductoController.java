package pe.edu.vallegrande.kardex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.kardex.model.Producto;
import pe.edu.vallegrande.kardex.service.ProductoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;
    
    @GetMapping
    public Flux<Producto> getAllProductos() {
        return productoService.findAllActive();
    }
    
    @GetMapping("/{id}")
    public Mono<Producto> getProductoById(@PathVariable String id) {
        return productoService.findById(id);
    }
    
    @GetMapping("/sku/{sku}")
    public Mono<Producto> getProductoBySku(@PathVariable String sku) {
        return productoService.findBySku(sku);
    }
    
    @GetMapping("/categoria/{categoria}")
    public Flux<Producto> getProductosByCategoria(@PathVariable String categoria) {
        return productoService.findByCategoria(categoria);
    }
    
    @GetMapping("/marca/{marca}")
    public Flux<Producto> getProductosByMarca(@PathVariable String marca) {
        return productoService.findByMarca(marca);
    }
    
    @GetMapping("/stock-bajo")
    public Flux<Producto> getProductosStockBajo() {
        return productoService.findProductosStockBajo();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Producto> createProducto(@RequestBody Producto producto) {
        return productoService.save(producto);
    }
    
    @PutMapping("/{id}")
    public Mono<Producto> updateProducto(@PathVariable String id, @RequestBody Producto producto) {
        return productoService.update(id, producto);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Producto> deleteProducto(@PathVariable String id) {
        return productoService.delete(id);
    }
    
    // Endpoint para SSE (Server-Sent Events) - Streaming de productos
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Producto> streamAllProductos() {
        return productoService.findAllActive()
                .delayElements(Duration.ofMillis(100));
    }
    
    // Endpoint para generar reporte de productos con stock bajo (para exportación)
    @GetMapping("/reporte/stock-bajo")
    public Flux<Producto> reporteProductosStockBajo() {
        return productoService.findProductosStockBajo();
    }
}