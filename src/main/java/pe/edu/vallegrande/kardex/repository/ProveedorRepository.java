package pe.edu.vallegrande.kardex.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.kardex.model.Proveedor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProveedorRepository extends ReactiveMongoRepository<Proveedor, String> {
    // Buscar por RUC
    Mono<Proveedor> findByRuc(String ruc);
    
    // Buscar por razón social
    Flux<Proveedor> findByRazonSocialContainingIgnoreCase(String razonSocial);
    
    // Buscar por nombre comercial
    Flux<Proveedor> findByNombreComercialContainingIgnoreCase(String nombreComercial);
    
    // Buscar por país
    Flux<Proveedor> findByPais(String pais);
    
    // Buscar por país y no eliminados
    Flux<Proveedor> findByPaisAndIsDeletedFalseOrIsDeletedIsNull(String pais);
    
    // Buscar proveedores no eliminados lógicamente
    Flux<Proveedor> findByIsDeletedFalse();
    
    // Buscar proveedores no eliminados lógicamente (incluyendo null)
    Flux<Proveedor> findByIsDeletedFalseOrIsDeletedIsNull();
    
    // Verificar si existe un proveedor con el mismo RUC
    Mono<Boolean> existsByRuc(String ruc);
}