package com.example.sebastian.recone.models;

import java.util.ArrayList;

/**
 * Created by Sebastian on 25/10/2016.
 */

public class Objeto {

    private  ArrayList<String> palabras;
    private ArrayList<Integer> siguientes;
    private boolean esRegulable;


    public Objeto(){
        palabras = new ArrayList<String>();
        siguientes = new ArrayList<Integer>();
        esRegulable = false;
    }

    public Objeto(ArrayList<String> palabras, int[] siguientes, boolean esRegulable){
       this.palabras = palabras;
        this.siguientes = new ArrayList<Integer>();
        for (int i = 0; i < siguientes.length; i++) {
            this.siguientes.add(siguientes[i]);
        }
        this.esRegulable = esRegulable;
    }

    public ArrayList<Integer> getSiguientes(){
        return siguientes;
    }

    public boolean isEsRegulable() {
        return esRegulable;
    }

    public ArrayList<String> getPalabras() {
        return palabras;
    }
}
