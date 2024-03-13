package com.ugb.tiendaonlineproductos;


public class productos{

    String idProducto;
    String codigo;
    String nombre;
    String marca;
    String precio;
    String descripcion;
    String imgproducto;

    public productos(String idProducto, String codigo, String nombre, String marca, String precio, String descripcion, String imgproducto) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imgproducto = imgproducto;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgproducto() {
        return imgproducto;
    }

    public void setImgproducto(String imgproducto) {
        this.imgproducto = imgproducto;
    }
}