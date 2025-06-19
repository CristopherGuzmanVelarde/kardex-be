package pe.edu.vallegrande.kardex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.kardex.model.Inventario;
import pe.edu.vallegrande.kardex.model.Producto;
import pe.edu.vallegrande.kardex.repository.InventarioRepository;
import pe.edu.vallegrande.kardex.repository.ProductoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    
    // Obtener todos los movimientos de inventario
    public Flux<Inventario> findAll() {
        return inventarioRepository.findAll()
                .filter(inventario -> !Boolean.TRUE.equals(inventario.getIsDeleted()));
    }
    
    // Buscar movimiento por ID
    public Mono<Inventario> findById(String id) {
        return inventarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movimiento de inventario no encontrado con ID: " + id)));
    }
    
    // Buscar movimientos por SKU de producto
    public Flux<Inventario> findBySkuProducto(String skuProducto) {
        return inventarioRepository.findBySkuProducto(skuProducto);
    }
    
    // Buscar movimientos por tipo de transacción
    public Flux<Inventario> findByTipoTransaccion(Inventario.TipoTransaccion tipoTransaccion) {
        return inventarioRepository.findByTipoTransaccion(tipoTransaccion);
    }
    
    // Buscar movimientos por rango de fechas
    public Flux<Inventario> findByFechaTransaccionBetween(LocalDateTime inicio, LocalDateTime fin) {
        return inventarioRepository.findByFechaTransaccionBetween(inicio, fin);
    }
    
    // Alias para findByFechaTransaccionBetween para uso en reportes
    public Flux<Inventario> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return findByFechaTransaccionBetween(inicio, fin);
    }
    
    // Registrar un nuevo movimiento de inventario
    public Mono<Inventario> save(Inventario inventario) {
        // Validaciones
        if (inventario.getSkuProducto() == null || inventario.getSkuProducto().trim().isEmpty()) {
            return Mono.error(new RuntimeException("El SKU del producto es obligatorio"));
        }
        
        if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
            return Mono.error(new RuntimeException("La cantidad debe ser mayor a cero"));
        }
        
        if (inventario.getTipoTransaccion() == null) {
            return Mono.error(new RuntimeException("El tipo de transacción es obligatorio"));
        }
        
        // Establecer fecha actual si no se proporciona
        if (inventario.getFechaTransaccion() == null) {
            inventario.setFechaTransaccion(LocalDateTime.now());
        }
        
        // Valores por defecto
        if (inventario.getIsDeleted() == null) {
            inventario.setIsDeleted(false);
        }
        
        // Verificar que el producto existe y actualizar su stock
        return productoRepository.findBySku(inventario.getSkuProducto())
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con SKU: " + inventario.getSkuProducto())))
                .flatMap(producto -> {
                    // Actualizar stock del producto según el tipo de transacción
                    return actualizarStockProducto(producto, inventario)
                            .then(inventarioRepository.save(inventario));
                });
    }
    
    // Método privado para actualizar el stock del producto
    private Mono<Producto> actualizarStockProducto(Producto producto, Inventario inventario) {
        Integer stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
        
        switch (inventario.getTipoTransaccion()) {
            case COMPRA:
            case DEVOLUCION:
                // Aumentar stock
                producto.setStockActual(stockActual + inventario.getCantidad());
                break;
            case VENTA:
                // Verificar si hay suficiente stock
                if (stockActual < inventario.getCantidad()) {
                    return Mono.error(new RuntimeException("Stock insuficiente para realizar la venta"));
                }
                // Disminuir stock
                producto.setStockActual(stockActual - inventario.getCantidad());
                break;
            case TRASLADO:
                // Para traslados, no modificamos el stock total, solo registramos el movimiento
                break;
        }
        
        return productoRepository.save(producto);
    }
    
    // Actualizar un movimiento de inventario
    public Mono<Inventario> update(String id, Inventario inventario) {
        return inventarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movimiento de inventario no encontrado con ID: " + id)))
                .flatMap(existingInventario -> {
                    // No permitimos cambiar el SKU del producto ni el tipo de transacción
                    // para mantener la integridad del inventario
                    if (!existingInventario.getSkuProducto().equals(inventario.getSkuProducto()) ||
                        !existingInventario.getTipoTransaccion().equals(inventario.getTipoTransaccion())) {
                        return Mono.error(new RuntimeException("No se puede modificar el SKU del producto ni el tipo de transacción"));
                    }
                    
                    // Si cambia la cantidad, debemos revertir el movimiento anterior y aplicar el nuevo
                    if (!existingInventario.getCantidad().equals(inventario.getCantidad())) {
                        return revertirMovimiento(existingInventario)
                                .then(Mono.defer(() -> {
                                    existingInventario.setCantidad(inventario.getCantidad());
                                    existingInventario.setDocumentoAsociado(inventario.getDocumentoAsociado());
                                    return productoRepository.findBySku(existingInventario.getSkuProducto())
                                            .flatMap(producto -> actualizarStockProducto(producto, existingInventario)
                                                    .then(inventarioRepository.save(existingInventario)));
                                }));
                    }
                    
                    // Si solo cambian otros campos, actualizamos directamente
                    existingInventario.setDocumentoAsociado(inventario.getDocumentoAsociado());
                    return inventarioRepository.save(existingInventario);
                });
    }
    
    // Método privado para revertir un movimiento de inventario
    private Mono<Producto> revertirMovimiento(Inventario inventario) {
        return productoRepository.findBySku(inventario.getSkuProducto())
                .flatMap(producto -> {
                    Integer stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                    
                    switch (inventario.getTipoTransaccion()) {
                        case COMPRA:
                        case DEVOLUCION:
                            // Revertir aumento de stock
                            producto.setStockActual(stockActual - inventario.getCantidad());
                            break;
                        case VENTA:
                            // Revertir disminución de stock
                            producto.setStockActual(stockActual + inventario.getCantidad());
                            break;
                        case TRASLADO:
                            // Para traslados, no modificamos el stock total
                            break;
                    }
                    
                    return productoRepository.save(producto);
                });
    }
    
    // Eliminar lógicamente un movimiento de inventario
    public Mono<Inventario> delete(String id) {
        return inventarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movimiento de inventario no encontrado con ID: " + id)))
                .flatMap(inventario -> {
                    inventario.setIsDeleted(true);
                    return revertirMovimiento(inventario)
                            .then(inventarioRepository.save(inventario));
                });
    }
    
    // Generar reporte de movimientos por período
    public Flux<Inventario> generarReporteMovimientosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return inventarioRepository.findByFechaTransaccionBetween(inicio, fin)
                .filter(inventario -> !Boolean.TRUE.equals(inventario.getIsDeleted()));
    }
    
    // Generar reporte de movimientos por producto
    public Flux<Inventario> generarReporteMovimientosPorProducto(String skuProducto) {
        return inventarioRepository.findBySkuProducto(skuProducto)
                .filter(inventario -> !Boolean.TRUE.equals(inventario.getIsDeleted()));
    }
}