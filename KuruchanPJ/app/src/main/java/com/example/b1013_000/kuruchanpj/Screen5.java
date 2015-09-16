package com.example.b1013_000.kuruchanpj;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
public class Screen5 extends Activity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataSet = new ArrayList<String>();
    private List<String> latitudeShop = new ArrayList<String>();
    private List<String> longitudeShop = new ArrayList<String>();
    private double lat, lng;
    private int rang;
    private String genreCD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen5);

        listView = (ListView) findViewById(R.id.listView2);

        //前画面の情報の取得
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("latitude", 0);
        lng = intent.getDoubleExtra("longitude", 0);
        rang = intent.getIntExtra("range", 0);
        genreCD = intent.getStringExtra("GenreCD");

        //LIstViewのためのアダプターの設定
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSet);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        //お店の情報の呼び出し
        loadCategory();


        System.out.println(dataSet);
    }

    private void loadCategory(){
        Uri baseUrl = Uri.parse("http://api.hotpepper.jp/GourmetSearch/V110/?key=guest&Latitude="+ lat + "&Longitude=" + lng + "&Range=" + rang + "&GenreCD=" + genreCD);
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
                            if(xpp.getName().equals("ShopName")){
                                System.out.println("お目当てのタグ発見！！"+xpp.getName());
                                xpp.next();
                                System.out.println(xpp.getText());
                                if(!(dataSet.contains(xpp.getText()))) {
                                    adapter.add(xpp.getText());
                                }
                            }else if(xpp.getName().equals("Latitude")){
                                xpp.next();
                                if(!(latitudeShop.contains(xpp.getText()))){
                                    latitudeShop.add(xpp.getText());
                                }
                            }else if(xpp.getName().equals("Longitude")){
                                xpp.next();
                                if(!(longitudeShop.contains(xpp.getText()))){
                                    longitudeShop.add(xpp.getText());
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(dataSet);
        Intent intent = new Intent(Screen5.this, MapsActivity.class);
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lng);
        intent.putExtra("latitudeShop", latitudeShop.get(position));
        intent.putExtra("longitudeShop", longitudeShop.get(position));
        startActivity(intent);

    }
}
