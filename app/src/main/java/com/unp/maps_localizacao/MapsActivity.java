package com.unp.maps_localizacao;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnTrocaRota, btnCalcularTaxa;
    private EditText edtOrigen, edtDestino, edtTaxaKm, edtTaxaMin;
    private TextView txtDuracao, txtDistancia, txtValor;//txtMIN, txtKM, txtTaxa

    private List<Marker> origenMakers = new ArrayList<>();
    private List<Marker> destinationMakers = new ArrayList<>();
    private List<Polyline> polyLinesPaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private double dist = 0;
    private double dura = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //instancias do objetos
        btnTrocaRota = (Button) findViewById(R.id.btnTrocaRota);
        edtOrigen = (EditText) findViewById(R.id.edtOrigen);
        edtDestino = (EditText) findViewById(R.id.edtDestino);
        txtDistancia = (TextView) findViewById(R.id.txtDistancia);
        txtDuracao = (TextView) findViewById(R.id.txtDuracao);

        //adicionado recentes
        btnCalcularTaxa = (Button) findViewById(R.id.btnCalcularTaxa);
        edtTaxaKm = (EditText) findViewById(R.id.edtTaxaKM);
        edtTaxaMin = (EditText) findViewById(R.id.edtTaxaMin);
        txtValor = (TextView) findViewById(R.id.txtValor);
//        txtMIN = (TextView) findViewById(R.id.txtMIN);
//        txtKM = (TextView) findViewById(R.id.txtKM);
//        txtTaxa = (TextView) findViewById(R.id.txtTaxa);

        //evendo do click do botao
        btnTrocaRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrocRota();
            }
        });
        btnCalcularTaxa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculaTaxa();
            }
        });

    }


    private void TrocRota() {
        String origem = edtOrigen.getText().toString();
        String destino = edtDestino.getText().toString();
        if (origem.isEmpty()) {
            msgToasts("Digite o endereo de origem");
        }

        if (destino.isEmpty()) {
            msgToasts("Digite o endereco de Destino");
        }

        try {
            new DirectionFinder(this, origem, destino).execute();
        } catch (UnsupportedEncodingException e) {
            msgToasts("Erro na execucao do Direction" + e);
        }

        //chamar metodo de direcao das rotas
    }

    private void msgToasts(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    private void calculaTaxa(){


        double ValFinal;
        String total;

        String taxaKmDigitada = edtTaxaKm.getText().toString();
        Float taxaKm = Float.parseFloat(taxaKmDigitada)/100;

        String taxaMinDigitada = edtTaxaMin.getText().toString();
        Float taxaMin = Float.parseFloat(taxaMinDigitada)/100;

        DecimalFormat format = new DecimalFormat("#0.00");



        double min = dist/60;
        double km = dura/1000;

        ValFinal = (min * taxaMin) + (km * taxaKm);

        total = String.format("#R$%.2f",ValFinal);

        txtValor.setText(total);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng unpRF = new LatLng(-5.861616, -35.192662);
        mMap.addMarker(new MarkerOptions().position(unpRF).title("UnP Roberto Freire"));

        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(unpRF).build(); // criei essa funcao
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); //segundo funcao

        mMap.moveCamera(CameraUpdateFactory.newLatLng(unpRF));
    }

    @Override
    public void onDirectionFinderStart() {

        progressDialog = progressDialog.show(this, "Aguarde!", "Traçando Rota...", true);

        if (origenMakers != null) {
            for (Marker marker : origenMakers) {
                marker.remove();
            }
        }

        if (destinationMakers != null) {
            for (Marker marker : destinationMakers) {
                marker.remove();
            }
        }

        if (polyLinesPaths != null) {
            for (Polyline polyline : polyLinesPaths) {
                polyline.remove();
            }

        }


    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polyLinesPaths = new ArrayList<>();
        origenMakers = new ArrayList<>();
        destinationMakers = new ArrayList<>();

        for (Route route : routes) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15));
            txtDuracao.setText(route.duration.text);
            txtDistancia.setText(route.distance.text);

            // macadores


            origenMakers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(route.startAddress)
                    .position(route.startLocation)
            ));

            destinationMakers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(route.endAddress)
                    .position(route.endLocation)
            ));

            //polyline

            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true)
                    .color(Color.RED).width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));

                dist = route.distance.value;
                dura = route.duration.value;
            }

            polyLinesPaths.add(mMap.addPolyline(polylineOptions));

        }

    }
}
