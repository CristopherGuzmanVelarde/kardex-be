package pe.edu.vallegrande.kardex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventario")
public class Inventario {
    @Id
    private String id;
    private String skuProducto;
    private Integer cantidad;
    private LocalDateTime fechaTransaccion;
    private TipoTransaccion tipoTransaccion;
    private String documentoAsociado; // Opcional
    private Boolean isDeleted;
    
    public enum TipoTransaccion {
        COMPRA, VENTA, DEVOLUCION, TRASLADO
    }
}