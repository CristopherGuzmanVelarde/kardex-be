package pe.edu.vallegrande.kardex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
public class Producto {
    @Id
    private String id;
    private String sku;
    private String nombre;
    private String marca;
    private String categoria;
    private String paisOrigen;
    private String codigoQR;
    private Boolean activo;
    private String especificacionesTecnicas;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String ubicacionAlmacen;
    private BigDecimal precioCompra;
    private BigDecimal precioVentaSugerido; // Con IGV automático
    private BigDecimal precioVentaFinal;
    private String imagenUrl;
    private Boolean isDeleted;
}