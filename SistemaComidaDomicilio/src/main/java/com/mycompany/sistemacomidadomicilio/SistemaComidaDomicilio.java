/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemacomidadomicilio;

/**
 *
 * @author rodri
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SistemaComidaDomicilio {

    private static final String USUARIOS_FILE = "usuarios.txt";
    private static final String RESERVAS_FILE = "reservas.txt";

    private static ArrayList<Usuario> usuarios = new ArrayList<>();
    private static ArrayList<Reserva> reservas = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Agregar usuario");
            System.out.println("2. Agregar reserva");
            System.out.println("3. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine();  

            switch (opcion) {
                case 1:
                    agregarUsuario(scanner);
                    break;
                case 2:
                    agregarReserva(scanner);
                    break;
                case 3:
                    guardarReservasConcurrente();
                    return;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        }
    }
    

    private static synchronized void agregarUsuario(Scanner scanner) {
    System.out.println("Ingrese cédula:");
    String cedula = scanner.nextLine();

    if (buscarUsuarioPorCedula(cedula) != null) {
        System.out.println("La cédula ya existe. No se puede agregar el mismo usuario.");
        return;
    }

    System.out.println("Ingrese nombre:");
    String nombre = scanner.nextLine();

    System.out.println("Ingrese número telefónico:");
    String telefono = scanner.nextLine();

    Usuario nuevoUsuario = new Usuario(cedula, nombre, telefono);
    usuarios.add(nuevoUsuario);

    System.out.println("Usuario agregado exitosamente.");
}

    private static void agregarReserva(Scanner scanner) {
        System.out.println("Ingrese número de cédula del usuario:");
        String cedula = scanner.nextLine();

        Usuario usuario = buscarUsuarioPorCedula(cedula);
        if (usuario == null) {
            System.out.println("Usuario no encontrado. No se puede realizar la reserva.");
            return;
        }

        System.out.println("Ingrese fecha de comida (DD/MM/YYYY):");
        String fechaComida = scanner.nextLine();

        System.out.println("Ingrese tipo de comida (Desayuno/Almuerzo/Cena):");
        String tipoComida = scanner.nextLine();

        System.out.println("Ingrese guarnición 1 (Arroz/Frijoles/Pancakes/Frutas):");
        String guarnicion1 = scanner.nextLine();

        System.out.println("Ingrese guarnición 2 (Arroz/Frijoles/Pancakes/Frutas):");
        String guarnicion2 = scanner.nextLine();

        System.out.println("Ingrese proteína (Carne/Pescado/Pollo/Huevos):");
        String proteina = scanner.nextLine();

        System.out.println("Ingrese ensalada (Verde/Rusa):");
        String ensalada = scanner.nextLine();

        Reserva nuevaReserva = new Reserva(cedula, fechaComida, tipoComida, guarnicion1, guarnicion2, proteina, ensalada);
        reservas.add(nuevaReserva);

        System.out.println("Reserva agregada exitosamente.");
    }
    
    private static void mostrarReservas() {
        
    Map<String, List<Reserva>> reservasPorFecha = new HashMap<>();

    for (Reserva reserva : reservas) {
        reservasPorFecha
            .computeIfAbsent(reserva.fechaComida, k -> new ArrayList<>())
            .add(reserva);
    }

    for (Map.Entry<String, List<Reserva>> entry : reservasPorFecha.entrySet()) {
        String fechaComida = entry.getKey();
        List<Reserva> reservasEnFecha = entry.getValue();

        System.out.println("Fecha de comida: " + fechaComida);

        for (Reserva reserva : reservasEnFecha) {
            System.out.println("Cédula: " + reserva.cedula);
            System.out.println("Tipo de comida: " + reserva.tipoComida);
            System.out.println("Guarnicion 1: " + reserva.guarnicion1);
            System.out.println("Guarnicion 2: " + reserva.guarnicion2);
            System.out.println("Proteína: " + reserva.proteina);
            System.out.println("Ensalada: " + reserva.ensalada);
            System.out.println();
        }
    }
}



    private static Usuario buscarUsuarioPorCedula(String cedula) {
        for (Usuario usuario : usuarios) {
            if (usuario.cedula.equals(cedula)) {
                return usuario;
            }
        }
        return null;
    }

    private static void guardarReservasConcurrente() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (Reserva reserva : reservas) {
            Runnable worker = new ReservaWorker(reserva);
            executorService.execute(worker);
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Esperar a que todos los hilos terminen
        }

        System.out.println("Todas las reservas han sido guardadas exitosamente.");
    }

    static class ReservaWorker implements Runnable {
        private final Reserva reserva;

        public ReservaWorker(Reserva reserva) {
            this.reserva = reserva;
        }

        @Override
        public void run() {
            synchronized (RESERVAS_FILE) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVAS_FILE, true))) {
                    writer.println(reserva.cedula);
                    writer.println("Fecha de comida: " + reserva.fechaComida);
                    writer.println("Tipo de comida: " + reserva.tipoComida);
                    writer.println("Guarnicion 1: " + reserva.guarnicion1);
                    writer.println("Guarnicion 2: " + reserva.guarnicion2);
                    writer.println("Proteína: " + reserva.proteina);
                    writer.println("Ensalada: " + reserva.ensalada);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
