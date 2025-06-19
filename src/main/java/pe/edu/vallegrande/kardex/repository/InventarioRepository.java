package pe.edu.vallegrande.kardex.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.kardex.model.Inventario;
import pe.edu.vallegrande.kardex.model.Inventario.TipoTransaccion;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface InventarioRepository extends ReactiveMongoRepository<Inventario, String> {
    // Buscar movimientos por SKU de producto
    Flux<Inventario> findBySkuProducto(String skuProducto);
    
    // Buscar movimientos por tipo de transacción
    Flux<Inventario> findByTipoTransaccion(TipoTransaccion tipoTransaccion);
    
    // Buscar movimientos por rango de fechas
    Flux<Inventario> findByFechaTransaccionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Buscar movimientos no eliminados lógicamente
    Flux<Inventario> findByIsDeletedFalse();
    
    // Buscar movimientos por SKU y tipo de transacción
    Flux<Inventario> findBySkuProductoAndTipoTransaccion(String skuProducto, TipoTransaccion tipoTransaccion);
}