package com.example.sebastian.recone.models;

import java.util.ArrayList;

/**
 * Created by Sebastian on 25/10/2016.
 */

public class Lugar {

    private  ArrayList<String> palabras;
    private ArrayList<Integer> siguientes;

    public Lugar(){
        palabras = new ArrayList<>();
        siguientes = new ArrayList<Integer>();
    }

    public Lugar(ArrayList<String> palabras, int[] siguientes){
       this.palabras   = palabras;
        this.siguientes = new ArrayList<Integer>();
        for (int i = 0; i < siguientes.length; i++) {
            this.siguientes.add(siguientes[i]);
        }
    }



    public ArrayList<Integer> getSiguientes(){
        return siguientes;
    }

    public ArrayList<String> getPalabras() {
        return palabras;
    }

    public void setPalabras(ArrayList<String> palabras) {
        this.palabras = palabras;
    }
}
