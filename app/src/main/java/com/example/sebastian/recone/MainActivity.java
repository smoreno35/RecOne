package com.example.sebastian.recone;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.sebastian.recone.models.Instruccion;
import com.example.sebastian.recone.models.Lugar;
import com.example.sebastian.recone.models.Objeto;
import com.example.sebastian.recone.viewholders.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ArrayList<Instruccion> instrucciones;
    ArrayList<Objeto> objetos;
    ArrayList<Lugar> lugares;

    FirebaseDatabase database;
    FloatingActionButton reconocedorBtn;

    SpeechRecognizer recognizer;
    Intent recognizerIntent;
    ArrayList<String> resultStringArrayList;

    private FirebaseRecyclerAdapter<Integer, ItemViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reconocedorBtn= (FloatingActionButton) findViewById(R.id.ibtn);

        mRecycler = (RecyclerView) findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        instrucciones = new ArrayList<Instruccion>();
        objetos = new ArrayList<Objeto>();
        lugares = new ArrayList<Lugar>();

        database = FirebaseDatabase.getInstance();
        DatabaseReference refLugares = database.getReference("lugares");
        DatabaseReference refObjetos = database.getReference("objetos");
        DatabaseReference refInstrucciones = database.getReference("instrucciones");

        ValueEventListener lugaresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                lugares.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lugar lugar = postSnapshot.getValue(Lugar.class);
                    lugares.add(lugar);
                }
                Log.d("MainACtivity", lugares.toString());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        refLugares.addValueEventListener(lugaresListener);

        ValueEventListener objetosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                objetos.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Objeto objeto = postSnapshot.getValue(Objeto.class);
                    objetos.add(objeto);

                }
                Log.d("MainACtivity", objetos.toString());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        refObjetos.addValueEventListener(objetosListener);

        ValueEventListener instruccionesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                instrucciones.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Instruccion instruccion = postSnapshot.getValue(Instruccion.class);
                    instrucciones.add(instruccion);
                }
                Log.d("MainACtivity", instrucciones.toString());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        refInstrucciones.addValueEventListener(instruccionesListener);

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new Intent(RecognizerIntent.EXTRA_LANGUAGE_MODEL);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.example.sebastian.recone");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,20);
        recognizer.setRecognitionListener(recognitionListener);
        reconocedorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               recognizer.startListening(recognizerIntent);
            }
        });


        DatabaseReference refcasa = database.getReference("casas/0");
        Query postsQuery = getQuery(refcasa);

        mAdapter = new FirebaseRecyclerAdapter<Integer, ItemViewHolder>(Integer.class, R.layout.item_list,
                ItemViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final Integer model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                String itemKey = postRef.getKey();
                String[] spl = itemKey.split("-");
                itemKey = spl[0]+"/"+spl[1];

                final int itemValue = model;
                Log.d(TAG,itemKey+" "+itemValue);

                viewHolder.bindToItem(itemKey,String.valueOf(itemValue));

            }
        };

        mRecycler.setAdapter(mAdapter);
    }

    public  Query getQuery(DatabaseReference databaseReference){
        return databaseReference.orderByKey();
    };

    void procesarVoz(ArrayList<String> array){

        Toast.makeText(this, array.get(0), Toast.LENGTH_LONG).show();
        ArrayList<Integer> lstObjetos =  new ArrayList<Integer>();
        ArrayList<Integer> lstLugares =  new ArrayList<Integer>();
        int instruccion=-1, objeto=-1, lugar=-1;
        int estado=0;
        boolean esRegulable = false;
        String comando = array.get(0);

        viewChanger(true,"Tap button");

        ArrayList<String> palabras = new ArrayList<String>( Arrays.asList(comando.split(" ")) );
        Log.d("MainActivity","antes: "+palabras.toString());
        palabras = limpiarTexto(palabras);
        Log.d("MainActivity","despues: "+palabras.toString());

        for (int i = 0; i < instrucciones.size() && instruccion==-1; i++) {
            for (int j = 0; j < instrucciones.get(i).getPalabras().size(); j++) {
                if(comparaPalabras(instrucciones.get(i).getPalabras().get(j) , palabras.get(0) )>=75){
                    lstObjetos = instrucciones.get(i).getSiguientes();
                    instruccion = i;
                    break;
                }
            }

        }

        if(instruccion==-1){
            //NO se ha encontrado la instrucción
            //TODO terminar esto
            Log.e("MainActivity","Error instruccion");
            return;
        }



        for (int i = 0; i < lstObjetos.size()&& objeto==-1; i++) {
            for (int j = 0; j < objetos.get(lstObjetos.get(i)).getPalabras().size() ; j++) {
                if (comparaPalabras(objetos.get(lstObjetos.get(i)).getPalabras().get(j), palabras.get(1)) >= 75) {
                    lstLugares = objetos.get(lstObjetos.get(i)).getSiguientes();
                    objeto = lstObjetos.get(i);
                    esRegulable = objetos.get(lstObjetos.get(i)).isEsRegulable();
                    break;
                }
            }
        }

        if(objeto==-1){
            //NO se ha encontrado la instrucción
            //TODO terminar esto

            Log.e("MainActivity","Error objeto");
            return;
        }

        for (int i = 0; i < lstLugares.size()&& lugar==-1; i++) {
            for (int j = 0; j < lugares.get(lstLugares.get(i)).getPalabras().size(); j++) {
                if(comparaPalabras(lugares.get(lstLugares.get(i)).getPalabras().get(j) , palabras.get(2) )>=75){
                    lugar= lstLugares.get(i);
                    break;
                }
            }
            //TODO SI está entre 60 y 75 preguntar!!
        }

        if(lugar==-1){
            //NO se ha encontrado la instrucción
            //TODO terminar esto
            Log.e("MainActivity","Error lugar");
            return;
        }

        if(esRegulable && palabras.size()>=4){
            estado = Integer.parseInt(palabras.get(3));
        }else{
            estado = instrucciones.get(instruccion).getEstado();
        }
        String com =  objeto+"-"+lugar;
        Toast.makeText(this, objeto+"-"+lugar+":"+estado, Toast.LENGTH_LONG).show();

        DatabaseReference ref = database.getReference("casas/0");
        ref.child(com).setValue(estado);

        Log.d("MainActivity", "Mensaje: "+instruccion+"/"+objeto+"/"+lugar);



    }

    int comparaPalabras(String palabra1, String palabra2){
        Log.d("MainActivity", "Compara Palabras: "+palabra1 +" y " +palabra2);
        if(palabra1.equals(palabra2)){
            Log.d("MainActivity", "Res: "+100);
            return 100;
        }

        float lev = levenshteinDistance(palabra1, palabra2);
        float size = palabra2.length();
        if(palabra1.length()>palabra2.length()){
            size = palabra1.length();
        }

        float res = ((size-lev)/size)*100;
        Log.d("MainActivity", "size: "+size+" lev: "+lev+" res: "+res);
        return (int)res;
    }

    public int levenshteinDistance (CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

    ArrayList<String> limpiarTexto(ArrayList<String> palabras){
        ArrayList<String> textoLimpiado = new ArrayList<String>();
        String original = "áéíóúABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
        String ascii = "aeiouabcdefghijklmnñopqrstuvwxyz";
        String output;
        for (String palabra:palabras) {
            output = palabra;
            for (int i = 0; i<original.length(); i++) {
                output = output.replace(original.charAt(i),ascii.charAt(i));
            }
            textoLimpiado.add(output);
        }
        // TODO Limpiar de preposiciones
        return textoLimpiado;
    }


    private void viewChanger(boolean isEnabled, String s){
        reconocedorBtn.setEnabled(isEnabled);
    }


    RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {
            viewChanger(false,"Escuchando...");

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            viewChanger(true, "Procesando...");
        }

        @Override
        public void onError(int error) {
            viewChanger(true,"Error");
        }

        @Override
        public void onResults(Bundle results) {
            resultStringArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            Log.d("MainActivity",resultStringArrayList.toString());
           //reconocedorBtn.setProgress(100);
            procesarVoz(resultStringArrayList);

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };



}

