package pe.edu.vallegrande.kardex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.kardex.model.Proveedor;
import pe.edu.vallegrande.kardex.service.ProveedorService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {
    private final ProveedorService proveedorService;
    
    @GetMapping
    public Flux<Proveedor> getAllProveedores() {
        return proveedorService.findAllActive();
    }
    
    @GetMapping("/{id}")
    public Mono<Proveedor> getProveedorById(@PathVariable String id) {
        return proveedorService.findById(id);
    }
    
    @GetMapping("/ruc/{ruc}")
    public Mono<Proveedor> getProveedorByRuc(@PathVariable String ruc) {
        return proveedorService.findByRuc(ruc);
    }
    
    @GetMapping("/razon-social/{razonSocial}")
    public Flux<Proveedor> getProveedoresByRazonSocial(@PathVariable String razonSocial) {
        return proveedorService.findByRazonSocial(razonSocial);
    }
    
    @GetMapping("/nombre-comercial/{nombreComercial}")
    public Flux<Proveedor> getProveedoresByNombreComercial(@PathVariable String nombreComercial) {
        return proveedorService.findByNombreComercial(nombreComercial);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Proveedor> createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.save(proveedor);
    }
    
    @PutMapping("/{id}")
    public Mono<Proveedor> updateProveedor(@PathVariable String id, @RequestBody Proveedor proveedor) {
        return proveedorService.update(id, proveedor);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Proveedor> deleteProveedor(@PathVariable String id) {
        return proveedorService.delete(id);
    }
    
    // Endpoint para SSE (Server-Sent Events) - Streaming de proveedores
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Proveedor> streamAllProveedores() {
        return proveedorService.findAllActive()
                .delayElements(Duration.ofMillis(100));
    }
    
    // Endpoint para generar reporte de proveedores por país
    @GetMapping("/reporte/pais/{pais}")
    public Flux<Proveedor> reporteProveedoresPorPais(@PathVariable String pais) {
        return proveedorService.generarReporteProveedoresPorPais(pais);
    }
}