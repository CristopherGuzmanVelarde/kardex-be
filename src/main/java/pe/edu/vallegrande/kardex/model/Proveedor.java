package pe.edu.vallegrande.kardex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "proveedores")
public class Proveedor {
    @Id
    private String id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String contactoPrincipal;
    private String emailContacto;
    private String telefonoContacto;
    private String direccionFiscal;
    private String ciudad;
    private String pais;
    private String terminosPago;
    private String observaciones;
    private Boolean isDeleted;
}