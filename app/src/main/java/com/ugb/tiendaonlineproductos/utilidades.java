package com.ugb.tiendaonlineproductos;

import java.util.Base64;

public class utilidades {
    //ipconfig para revisar la direccion de la maquina, puede cambiar
    static String urlConsulta = "http://192.168.1.2:5984/producto/_design/producto/_view/producto";
    static String urlMto = "http://192.168.1.2:5984/producto";
    static String user = "Cindy";
    static String passwd = "couch129.29";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}