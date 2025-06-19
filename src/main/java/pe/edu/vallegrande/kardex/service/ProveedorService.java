package pe.edu.vallegrande.kardex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.kardex.model.Proveedor;
import pe.edu.vallegrande.kardex.repository.ProveedorRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    
    // Obtener todos los proveedores activos
    public Flux<Proveedor> findAllActive() {
        return proveedorRepository.findByIsDeletedFalseOrIsDeletedIsNull();
    }
    
    // Obtener todos los proveedores (incluyendo eliminados)
    public Flux<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }
    
    // Buscar proveedor por ID
    public Mono<Proveedor> findById(String id) {
        return proveedorRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Proveedor no encontrado con ID: " + id)));
    }
    
    // Buscar proveedor por RUC
    public Mono<Proveedor> findByRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc)
                .switchIfEmpty(Mono.error(new RuntimeException("Proveedor no encontrado con RUC: " + ruc)));
    }
    
    // Buscar proveedores por razón social
    public Flux<Proveedor> findByRazonSocial(String razonSocial) {
        return proveedorRepository.findByRazonSocialContainingIgnoreCase(razonSocial);
    }
    
    // Buscar proveedores por nombre comercial
    public Flux<Proveedor> findByNombreComercial(String nombreComercial) {
        return proveedorRepository.findByNombreComercialContainingIgnoreCase(nombreComercial);
    }
    
    // Crear un nuevo proveedor
    public Mono<Proveedor> save(Proveedor proveedor) {
        // Validaciones
        if (proveedor.getRuc() == null || proveedor.getRuc().trim().isEmpty()) {
            return Mono.error(new RuntimeException("El RUC del proveedor es obligatorio"));
        }
        
        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            return Mono.error(new RuntimeException("La razón social del proveedor es obligatoria"));
        }
        
        // Validar formato de RUC (11 dígitos para Perú)
        if (!proveedor.getRuc().matches("\\d{11}")) {
            return Mono.error(new RuntimeException("El RUC debe tener 11 dígitos numéricos"));
        }
        
        // Valores por defecto
        if (proveedor.getIsDeleted() == null) {
            proveedor.setIsDeleted(false);
        }
        
        // Verificar si ya existe un proveedor con el mismo RUC
        return proveedorRepository.findByRuc(proveedor.getRuc())
                .flatMap(existingProveedor -> Mono.error(new RuntimeException("Ya existe un proveedor con el RUC: " + proveedor.getRuc())))
                .switchIfEmpty(proveedorRepository.save(proveedor))
                .cast(Proveedor.class);
    }
    
    // Actualizar un proveedor existente
    public Mono<Proveedor> update(String id, Proveedor proveedor) {
        return proveedorRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Proveedor no encontrado con ID: " + id)))
                .flatMap(existingProveedor -> {
                    // No permitimos cambiar el RUC para mantener la integridad
                    if (!existingProveedor.getRuc().equals(proveedor.getRuc())) {
                        return Mono.error(new RuntimeException("No se puede modificar el RUC del proveedor"));
                    }
                    
                    // Actualizar campos
                    existingProveedor.setRazonSocial(proveedor.getRazonSocial());
                    existingProveedor.setNombreComercial(proveedor.getNombreComercial());
                    existingProveedor.setContactoPrincipal(proveedor.getContactoPrincipal());
                    existingProveedor.setEmailContacto(proveedor.getEmailContacto());
                    existingProveedor.setTelefonoContacto(proveedor.getTelefonoContacto());
                    existingProveedor.setDireccionFiscal(proveedor.getDireccionFiscal());
                    existingProveedor.setCiudad(proveedor.getCiudad());
                    existingProveedor.setPais(proveedor.getPais());
                    existingProveedor.setTerminosPago(proveedor.getTerminosPago());
                    existingProveedor.setObservaciones(proveedor.getObservaciones());
                    
                    return proveedorRepository.save(existingProveedor);
                });
    }
    
    // Eliminar lógicamente un proveedor
    public Mono<Proveedor> delete(String id) {
        return proveedorRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Proveedor no encontrado con ID: " + id)))
                .flatMap(proveedor -> {
                    proveedor.setIsDeleted(true);
                    return proveedorRepository.save(proveedor);
                });
    }
    
    // Eliminar físicamente un proveedor
    public Mono<Void> deletePhysically(String id) {
        return proveedorRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Proveedor no encontrado con ID: " + id)))
                .flatMap(proveedor -> proveedorRepository.delete(proveedor));
    }
    
    // Generar reporte de proveedores por país
    public Flux<Proveedor> generarReporteProveedoresPorPais(String pais) {
        return proveedorRepository.findByPaisAndIsDeletedFalseOrIsDeletedIsNull(pais);
    }
}