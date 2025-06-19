package pe.edu.vallegrande.kardex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.kardex.model.Producto;
import pe.edu.vallegrande.kardex.repository.ProductoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    
    // Obtener todos los productos activos
    public Flux<Producto> findAllActive() {
        return productoRepository.findAll()
                .filter(producto -> !Boolean.TRUE.equals(producto.getIsDeleted()));
    }
    
    // Obtener todos los productos (incluyendo eliminados)
    public Flux<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    // Buscar producto por ID
    public Mono<Producto> findById(String id) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + id)));
    }
    
    // Buscar producto por SKU
    public Mono<Producto> findBySku(String sku) {
        return productoRepository.findBySku(sku)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con SKU: " + sku)));
    }
    
    // Buscar productos por categoría
    public Flux<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    // Buscar productos por marca
    public Flux<Producto> findByMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }
    
    // Crear un nuevo producto
    public Mono<Producto> save(Producto producto) {
        // Validaciones
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            return Mono.error(new RuntimeException("El nombre del producto es obligatorio"));
        }
        
        if (producto.getSku() == null || producto.getSku().trim().isEmpty()) {
            return Mono.error(new RuntimeException("El SKU del producto es obligatorio"));
        }
        
        // Calcular precio de venta sugerido (con IGV automático)
        if (producto.getPrecioCompra() != null && (producto.getPrecioVentaSugerido() == null || producto.getPrecioVentaSugerido().compareTo(BigDecimal.ZERO) == 0)) {
            // Aplicar un margen del 30% + IGV (18%)
            BigDecimal margen = new BigDecimal("1.30");
            BigDecimal igv = new BigDecimal("1.18");
            producto.setPrecioVentaSugerido(producto.getPrecioCompra().multiply(margen).multiply(igv).setScale(2, RoundingMode.HALF_UP));
        }
        
        // Valores por defecto
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        
        if (producto.getIsDeleted() == null) {
            producto.setIsDeleted(false);
        }
        
        // Verificar si ya existe un producto con el mismo SKU
        return productoRepository.findBySku(producto.getSku())
                .flatMap(existingProducto -> Mono.error(new RuntimeException("Ya existe un producto con el SKU: " + producto.getSku())))
                .switchIfEmpty(productoRepository.save(producto))
                .cast(Producto.class);
    }
    
    // Actualizar un producto existente
    public Mono<Producto> update(String id, Producto producto) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + id)))
                .flatMap(existingProducto -> {
                    // Actualizar campos
                    existingProducto.setNombre(producto.getNombre());
                    existingProducto.setMarca(producto.getMarca());
                    existingProducto.setCategoria(producto.getCategoria());
                    existingProducto.setPaisOrigen(producto.getPaisOrigen());
                    existingProducto.setCodigoQR(producto.getCodigoQR());
                    existingProducto.setActivo(producto.getActivo());
                    existingProducto.setEspecificacionesTecnicas(producto.getEspecificacionesTecnicas());
                    existingProducto.setStockActual(producto.getStockActual());
                    existingProducto.setStockMinimo(producto.getStockMinimo());
                    existingProducto.setStockMaximo(producto.getStockMaximo());
                    existingProducto.setUbicacionAlmacen(producto.getUbicacionAlmacen());
                    existingProducto.setPrecioCompra(producto.getPrecioCompra());
                    existingProducto.setPrecioVentaSugerido(producto.getPrecioVentaSugerido());
                    existingProducto.setPrecioVentaFinal(producto.getPrecioVentaFinal());
                    existingProducto.setImagenUrl(producto.getImagenUrl());
                    
                    return productoRepository.save(existingProducto);
                });
    }
    
    // Eliminar un producto (eliminación lógica)
    public Mono<Producto> delete(String id) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + id)))
                .flatMap(producto -> {
                    producto.setIsDeleted(true);
                    return productoRepository.save(producto);
                });
    }
    
    /**
     * Encuentra productos con stock por debajo del mínimo establecido
     */
    public Flux<Producto> findStockBajo() {
        return productoRepository.findAll()
                .filter(producto -> !Boolean.TRUE.equals(producto.getIsDeleted()))
                .filter(producto -> {
                    Integer stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                    Integer stockMinimo = producto.getStockMinimo() != null ? producto.getStockMinimo() : 0;
                    return stockActual < stockMinimo;
                });
    }
    
    /**
     * Calcula la valorización total del inventario
     */
    public Mono<Map<String, Object>> getValorizacionTotal() {
        return findAllActive()
                .filter(producto -> producto.getStockActual() != null && producto.getStockActual() > 0)
                .map(producto -> {
                    BigDecimal valorProducto = producto.getPrecioCompra() != null ? 
                            producto.getPrecioCompra().multiply(new BigDecimal(producto.getStockActual())) : 
                            BigDecimal.ZERO;
                    return valorProducto;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .map(valorTotal -> {
                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("valorTotal", valorTotal);
                    resultado.put("moneda", "PEN");
                    resultado.put("fecha", java.time.LocalDate.now().toString());
                    return resultado;
                });
    }
    
    // Eliminar físicamente un producto
    public Mono<Void> deletePhysically(String id) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + id)))
                .flatMap(producto -> productoRepository.delete(producto));
    }
    
    // Verificar si hay productos con stock bajo
    public Flux<Producto> findProductosStockBajo() {
        return productoRepository.findAll()
                .filter(producto -> !Boolean.TRUE.equals(producto.getIsDeleted()))
                .filter(producto -> producto.getStockActual() != null && 
                                   producto.getStockMinimo() != null && 
                                   producto.getStockActual() <= producto.getStockMinimo());
    }
}