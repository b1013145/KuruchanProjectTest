package com.example.b1013_000.kuruchanpj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;


public class RequestDirectionsTask extends AsyncTask<Void, Void, List<ParseJson.Route>> {


    private LatLng mOrigin; //出発地点の位置情報
    private LatLng mDest; //目的地の位置情報
    private String mTravelMode; //トラベルモードの設定(driving, walking, etc)
    private RequestDirectionsTaskCallback mCallback; //callback
    private Throwable mThrowable;

    //コンストラクタの設定
    public RequestDirectionsTask(Context context, LatLng origin, LatLng dest,
            String travelMode) {
        mOrigin = origin;
        mDest = dest;
        mTravelMode = travelMode;
    }

    @Override
    protected List<ParseJson.Route> doInBackground(Void... args) {

        //google directions APIによる経路のURLの取得
        String directionsUrl = getDirectionsUrl(mOrigin, mDest, mTravelMode);
        System.out.println(directionsUrl);
        try {
            //Http通信による経路の文字データの取得
            String result = reqeust(directionsUrl);

            List<ParseJson.Route> directions = ParseJson.parseDirections(result);
            System.out.println(directions);
            return directions;
        } catch (IOException e) {
            mThrowable = e;
        } catch (JSONException e) {
            mThrowable = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<ParseJson.Route> directions) {
        if (mThrowable == null) {
            mCallback.onSucceed(directions);
        } else {
            mCallback.onFailed(mThrowable);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, String travelMode) {
        Builder builder = Uri.parse(
                "https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon();

        builder.appendQueryParameter("origin", origin.latitude + ","
                + origin.longitude);
        builder.appendQueryParameter("destination", dest.latitude + ","
                + dest.longitude);
        builder.appendQueryParameter("language", "ja");
        builder.appendQueryParameter("sensor", "false");
        builder.appendQueryParameter("mode", travelMode);

        return builder.build().toString();
    }

    private String reqeust(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public RequestDirectionsTask setRequestDirectionsTaskCallback(RequestDirectionsTaskCallback l) {
        mCallback = l;

        return this;
    }

    public interface RequestDirectionsTaskCallback {
        public void onSucceed(List<ParseJson.Route> result);

        public void onFailed(Throwable e);
    }
}
