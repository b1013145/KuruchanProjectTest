package com.example.b1013_000.kuruchanpj;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double lat, lng;
    private String latitudeShop, longitudeShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("latitude", 0);
        lng = intent.getDoubleExtra("longitude", 0);
        latitudeShop = intent.getStringExtra("latitudeShop");
        longitudeShop = intent.getStringExtra("longitudeShop");
        Double a = Double.parseDouble(latitudeShop);
        Double b = Double.parseDouble(longitudeShop);

        LatLng origin = new LatLng(lat, lng);
        LatLng dest = new LatLng(a, b);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 17));

        routeSearch(origin, dest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private void loadCategory(){
        String str_origin = "origin=" + lat + "," + lng;
        String str_dest = "destination=" + latitudeShop + "," + longitudeShop;
        String sensor = "sensor=false";

        Uri baseUrl = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?"+ str_origin + "&" + str_dest);
        Uri uri = baseUrl;

        new AsyncTask<Uri, Void, String>(){

            @Override
            protected String doInBackground(Uri... params) {
                Uri uri = params[0];
                String result = request(uri);

                return result;
            }

            private String request(Uri uri){
                HttpURLConnection http = null;
                InputStream is = null;
                String result = null;
                try {
                    URL url = new URL(uri.toString());
                    http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("GET");
                    http.connect();
                    is = http.getInputStream();

                    result = toString(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                System.out.println(result);
            }

            private String toString(InputStream is) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                char[] b = new char[1024];
                int line;
                while(0 <= (line = reader.read(b))) {
                    sb.append(b, 0, line);
                }

                return sb.toString();
            }
        }.execute(uri);
    }

    private void routeSearch(LatLng origin, LatLng  dest){

        //非同期通信の継承クラスのRequestDirectionsTaskの呼び出し
        RequestDirectionsTask task = new RequestDirectionsTask(this, origin, dest, "walking");

        //callback関数の呼び出し
        task.setRequestDirectionsTaskCallback(
                new RequestDirectionsTask.RequestDirectionsTaskCallback() {


                    //ルート検索が成功したときに呼び出される関数
                    @Override
                    public void onSucceed(List<ParseJson.Route> routes) {

                        // ルートの描画開始
                        if (routes.size() > 0) {
                            PolylineOptions lineOptions = new PolylineOptions();
                            for (ParseJson.Route route : routes) {
                                for (ParseJson.Leg leg : route.getLegs()) {
                                    for (ParseJson.Step step : leg.getSteps()) {
                                        lineOptions.addAll(step.getPolylinePoints());
                                        lineOptions.width(20);
                                        lineOptions.color(0x550000ff);
                                    }
                                }
                            }
                            // 描画
                            mMap.addPolyline(lineOptions);
                        } else {
                            mMap.clear();
                            Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    //ルート検索が失敗したときに呼び出される関数
                    @Override
                    public void onFailed(Throwable e) {
                        Toast.makeText(MapsActivity.this,
                                "ルート検索中にエラーが発生しました。(" + e + ")",
                                Toast.LENGTH_LONG).show();
                    }
                }).execute();
    }

}
