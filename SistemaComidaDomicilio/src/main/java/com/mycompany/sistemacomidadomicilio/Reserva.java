/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacomidadomicilio;

/**
 *
 * @author rodri
 */
public class Reserva {
    String cedula;
    String fechaComida;
    String tipoComida;
    String guarnicion1;
    String guarnicion2;
    String proteina;
    String ensalada;

    public Reserva(String cedula, String fechaComida, String tipoComida, String guarnicion1, String guarnicion2,
                   String proteina, String ensalada) {
        this.cedula = cedula;
        this.fechaComida = fechaComida;
        this.tipoComida = tipoComida;
        this.guarnicion1 = guarnicion1;
        this.guarnicion2 = guarnicion2;
        this.proteina = proteina;
        this.ensalada = ensalada;
    }
}
