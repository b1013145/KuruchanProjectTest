package com.example.b1013_000.kuruchanpj;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b1013_000 on 2015/09/12.
 */
public class Screen4 extends Activity implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, LocationListener {
    private ListView listView;
    private SeekBar seekBar;
    private ArrayAdapter<String> adapter;
    private List<String> dataSet = new ArrayList<String>();
    private List<String> categoryCD = new ArrayList<String>();
    private LocationManager mLocationManager;
    private double lat, lng;
    private TextView tx;

    private int rang = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen4);

        //レイアウトからの対応付け
        listView = (ListView) findViewById(R.id.listView);
        seekBar = (SeekBar) findViewById(R.id.seek1);
        tx = (TextView) findViewById(R.id.distance);

        //textViewの初期値の設定
        tx.setText("1000m");

        //ロケーションのインスタンス生成
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mLocationManager.getBestProvider(criteria, true);

        //アダプターの設定
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSet);
        listView.setAdapter(adapter);

        //リスナーのセット
        listView.setOnItemClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);

        //HotpepperAPIからお店のカテゴリの追加
        loadCategory();
    }

    //リストに追加するためのカテゴリを取得するためのメソッド
    private void loadCategory(){
        Uri baseUrl = Uri.parse("http://api.hotpepper.jp/GourmetSearch/V110/?key=guest&Latitude="+ lat + "&Longitude=" + lng + "&Range=" + rang);
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
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);

                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result));

                    int eventType = xpp.getEventType();
                    while(eventType != XmlPullParser.END_DOCUMENT){
                        if(eventType == XmlPullParser.START_DOCUMENT) {
                            System.out.println("Start document");
                        } else if(eventType == XmlPullParser.START_TAG) {
                            if(xpp.getName().equals("GenreName")){
                                //System.out.println("お目当てのタグ発見！！"+xpp.getName());
                                xpp.next();
                                //System.out.println(xpp.getText());
                                if(!(dataSet.contains(xpp.getText()))) {
                                    adapter.add(xpp.getText());
                                }
                            }else if(xpp.getName().equals("GenreCD")){
                                xpp.next();
                                if(!(categoryCD.contains(xpp.getText()))){
                                    categoryCD.add(xpp.getText());
                                }

                            }
                        } else if(eventType == XmlPullParser.END_TAG){

                        } else if(eventType == XmlPullParser.TEXT){
                        }
                        eventType = xpp.next();
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        System.out.println(progress);
        if(progress>=0&&progress<20){
            rang = 1;
            tx.setText("300m");
        }else if(progress>=20&&progress<40){
            rang = 2;
            tx.setText("500m");
        }else if(progress>=40&&progress<60){
            rang = 3;
            tx.setText("1000m");
        }else if(progress>=60&&progress<80){
            rang = 4;
            tx.setText("2000m");
        }else if(progress>=80&&progress<=100){
            rang = 5;
            tx.setText("3000m");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        categoryCD.clear();
        adapter.clear();
        loadCategory();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(dataSet);
        Intent intent = new Intent(Screen4.this, Screen5.class);
        intent.putExtra("categoryName", dataSet.get(position));
        intent.putExtra("GenreCD", categoryCD.get(position));
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lng);
        System.out.println(lat);
        System.out.println(lng);
        intent.putExtra("range", rang);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
