package com.example.sebastian.recone.models;

import java.util.ArrayList;

/**
 * Created by Sebastian on 25/10/2016.
 */

public class Instruccion {

    private  ArrayList<String> palabras;
    private ArrayList<Integer> siguientes;
    private int estado;

    public Instruccion(){
        palabras  = new ArrayList<String>();
        siguientes = new ArrayList<Integer>();
        estado = 0;
    }

    public Instruccion(ArrayList<String> palabras, int[] siguientes, int estado){
       this.palabras = palabras;
        this.siguientes = new ArrayList<Integer>();
        for (int i = 0; i < siguientes.length; i++) {
            this.siguientes.add(siguientes[i]);
        }
        this.estado = estado;
    }



    public ArrayList<Integer> getSiguientes(){
        return siguientes;
    }

    public ArrayList<String> getPalabras() {
        return palabras;
    }

    public int getEstado() {
        return estado;
    }
}
