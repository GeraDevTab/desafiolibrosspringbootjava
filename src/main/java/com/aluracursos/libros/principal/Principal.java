package com.aluracursos.libros.principal;

import com.aluracursos.libros.model.Datos;
import com.aluracursos.libros.model.DatosLibros;
import com.aluracursos.libros.service.ConsumoAPI;
import com.aluracursos.libros.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    public  void muestraElMenu() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        //top 10 libros mas descargados
        System.out.println("Top 10 de libros mas descargados");
        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l ->l.titulo().toUpperCase())
                .forEach(System.out::println);

        //busqueda de libros por nombre
        System.out.println("Ingrese el nombre del libro qe desea buscar");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="+tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json,Datos.class);
        Optional< DatosLibros> libroBuscado = datosBusqueda.libros().stream()
                .filter(l ->l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            System.out.println("Libro Encontrado");
            System.out.println(libroBuscado.get());
            System.out.println("El titulo del libro es: "+libroBuscado.get().titulo());
        } else{
            System.out.println("Libro no encontrado");
        }

        //trabajando con estadisticas
        DoubleSummaryStatistics estadistica = datosBusqueda.libros().stream()
                .filter(d -> d.numeroDeDescargas()>0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("Cantidad media de descargas: "+estadistica.getAverage());
        System.out.println("Cantidad maxima de descargas: "+estadistica.getMax());
        System.out.println("Cantidad minima de descargas: "+estadistica.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: "+estadistica.getCount());
    }
}
