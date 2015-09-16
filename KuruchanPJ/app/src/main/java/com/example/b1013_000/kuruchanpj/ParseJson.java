package com.example.b1013_000.kuruchanpj;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class ParseJson {

    public static List<Route> parseDirections(String json) throws JSONException {
        JSONObject jsRoot = new JSONObject(json);
        String status = jsRoot.getString("status");
        if (!"OK".equals(status.toUpperCase(Locale.getDefault()))) {
            throw new RuntimeException("status not OK.");
        }

        JSONArray jsRoutes = jsRoot.getJSONArray("routes");

        ArrayList<Route> routeList = new ArrayList<Route>();
        for (int i = 0, iL = jsRoutes.length(); i < iL; i++) {
            //JSONデータからlegsを取り出す
            JSONObject jsRoute = jsRoutes.getJSONObject(i);
            JSONArray jsLegs = jsRoute.getJSONArray("legs");
            ArrayList<Leg> legList = new ArrayList<Leg>();

            for (int j = 0, jL = jsLegs.length(); j < jL; j++) {
                //JSONデータからlegsの中のstepsを取り出す
                JSONObject jsLeg = jsLegs.getJSONObject(j);
                JSONArray jsSteps = jsLeg.getJSONArray("steps");
                ArrayList<Step> stepList = new ArrayList<Step>(); //stepsのためのリスト

                for (int k = 0, kL = jsSteps.length(); k < kL; k++) {
                    //JSONデータのstepsの中のpolylineを取り出す

                    JSONObject jsStep = jsSteps.getJSONObject(k);
                    JSONObject jsPolyline = jsStep.getJSONObject("polyline");
                    String points = jsPolyline.getString("points");
                    List<LatLng> decodedPoly = decodePoly(points);

                    Step step = new Step();
                    step.setPolylinePoints(decodedPoly);
                    stepList.add(step);
                }

                Leg leg = new Leg();
                leg.setStartAddress(jsLeg.getString("start_address"));
                leg.setEndAddress(jsLeg.getString("end_address"));
                leg.setSteps(stepList);
                legList.add(leg);
            }

            Route route = new Route();
            route.setLegs(legList);
            routeList.add(route);
        }

        return routeList;
    }


    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public static class Route {
        private List<Leg> legs;

        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }
    }

    public static class Leg {
        private String startAddress;
        private String endAddress;
        private List<Step> steps;

        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    public static class Step {
        private List<LatLng> polylinePoints;

        public List<LatLng> getPolylinePoints() {
            return polylinePoints;
        }

        public void setPolylinePoints(List<LatLng> polylinePoints) {
            this.polylinePoints = polylinePoints;
        }
    }
}
