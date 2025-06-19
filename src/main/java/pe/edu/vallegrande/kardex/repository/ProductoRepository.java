package pe.edu.vallegrande.kardex.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.kardex.model.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {
    // Buscar por SKU
    Mono<Producto> findBySku(String sku);
    
    // Buscar por categoría
    Flux<Producto> findByCategoria(String categoria);
    
    // Buscar por marca
    Flux<Producto> findByMarca(String marca);
    
    // Buscar productos activos (no eliminados lógicamente)
    Flux<Producto> findByIsDeletedFalse();
    
    // Buscar productos con stock bajo (menor al mínimo)
    Flux<Producto> findByStockActualLessThanAndIsDeletedFalse(Integer stockMinimo);
    
    // Verificar si existe un producto con el mismo SKU
    Mono<Boolean> existsBySku(String sku);
}