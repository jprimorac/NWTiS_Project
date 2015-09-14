/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.rest.klijenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foi.nwtis.jprimora.web.podaci.Adresa;
import org.foi.nwtis.jprimora.web.podaci.Lokacija;
import org.foi.nwtis.jprimora.web.podaci.MeteoPodaci;

/**
 *
 * @author nwtis_1
 */
public class OWMKlijent {

    String apiKey;
    OWMRESTHelper helper;
    Client client;

    public OWMKlijent(String apiKey) {
        this.apiKey = apiKey;
        helper = new OWMRESTHelper(apiKey);
        client = ClientBuilder.newClient();
    }

    public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Current_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        //TODO složiti da ne puca ako nešto ne dođe
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JSONObject jo = new JSONObject(odgovor);
            MeteoPodaci mp = new MeteoPodaci();
            mp.setSunRise(new Date(jo.getJSONObject("sys").getLong("sunrise")));
            mp.setSunSet(new Date(jo.getJSONObject("sys").getLong("sunset")));

            mp.setTemperatureValue(Float.parseFloat(jo.getJSONObject("main").getString("temp")));
            mp.setTemperatureMin(Float.parseFloat(jo.getJSONObject("main").getString("temp_min")));
            mp.setTemperatureMax(Float.parseFloat(jo.getJSONObject("main").getString("temp_max")));
            mp.setTemperatureUnit("celsius");

            mp.setHumidityValue(Float.parseFloat(jo.getJSONObject("main").getString("humidity")));
            mp.setHumidityUnit("%");

            mp.setPressureValue(Float.parseFloat(jo.getJSONObject("main").getString("pressure")));
            mp.setPressureUnit(latitude);

            mp.setWindSpeedValue(Float.parseFloat(jo.getJSONObject("wind").getString("speed")));
            mp.setWindSpeedName("");

            mp.setWindDirectionValue(Float.parseFloat(jo.getJSONObject("wind").getString("deg")));
            mp.setWindDirectionCode("");
            mp.setWindDirectionName("");

            mp.setCloudsValue(jo.getJSONObject("clouds").getInt("all"));
            mp.setCloudsName(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
            mp.setPrecipitationMode("");

            mp.setWeatherNumber(jo.getJSONArray("weather").getJSONObject(0).getInt("id"));
            mp.setWeatherValue(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
            mp.setWeatherIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));

            mp.setLastUpdate(new Date(jo.getLong("dt")));

            return mp;

        } catch (JSONException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Adresa> getStationsNear(String latitude, String longitude, String broj) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_StationsNear_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("cnt", broj);
        webResource = webResource.queryParam("APIKEY", apiKey);

        //TODO složiti da ne puca ako nešto ne dođe
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JSONArray JSONpolje = new JSONArray(odgovor);
            List<Adresa> listaStanica = new ArrayList<>();
            for (int i = 0; i < JSONpolje.length(); i++) {
                JSONObject JSONStanica = JSONpolje.getJSONObject(i);
                Adresa stanica = new Adresa();
                stanica.setAdresa(JSONStanica.getJSONObject("station").getString("name"));
                Lokacija l = new Lokacija();
                l.setLatitude(JSONStanica.getJSONObject("station").getJSONObject("coord").getString("lat"));
                l.setLongitude(JSONStanica.getJSONObject("station").getJSONObject("coord").getString("lon"));
                stanica.setGeoloc(l);
                listaStanica.add(stanica);
            }

            return listaStanica;

        } catch (JSONException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MeteoPodaci> getHoursWeather(String latitude, String longitude, int broj) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Forecast_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        //TODO složiti da ne puca ako nešto ne dođe
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        List<MeteoPodaci> listaMeteo = new ArrayList<>();
        try {
            JSONObject JOdgovor = new JSONObject(odgovor);
            JSONArray list = JOdgovor.getJSONArray("list");
            int brojac = 0;
            for (int i = 0; i < list.length(); i++) {
                if (brojac == broj) {
                    break;
                }
                JSONObject jo = list.getJSONObject(i);
                for (int j = 0; j < 3; j++) {
                    if (brojac == broj) {
                        break;
                    }

                    MeteoPodaci mp = new MeteoPodaci();

                    mp.setTemperatureValue(Float.parseFloat(jo.getJSONObject("main").getString("temp")));
                    mp.setTemperatureMin(Float.parseFloat(jo.getJSONObject("main").getString("temp_min")));
                    mp.setTemperatureMax(Float.parseFloat(jo.getJSONObject("main").getString("temp_max")));
                    mp.setTemperatureUnit("celsius");

                    mp.setHumidityValue(Float.parseFloat(jo.getJSONObject("main").getString("humidity")));
                    mp.setHumidityUnit("%");

                    mp.setPressureValue(Float.parseFloat(jo.getJSONObject("main").getString("pressure")));
                    mp.setPressureUnit(latitude);

                    mp.setWindSpeedValue(Float.parseFloat(jo.getJSONObject("wind").getString("speed")));
                    mp.setWindSpeedName("");

                    mp.setWindDirectionValue(Float.parseFloat(jo.getJSONObject("wind").getString("deg")));
                    mp.setWindDirectionCode("");
                    mp.setWindDirectionName("");

                    mp.setCloudsValue(jo.getJSONObject("clouds").getInt("all"));
                    mp.setCloudsName(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                    mp.setPrecipitationMode("");

                    mp.setWeatherNumber(jo.getJSONArray("weather").getJSONObject(0).getInt("id"));
                    mp.setWeatherValue(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                    mp.setWeatherIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));

                    mp.setLastUpdate(new Date(jo.getLong("dt")));
                    listaMeteo.add(mp);

                }
            }

            return listaMeteo;

        } catch (JSONException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MeteoPodaci> getDaysWeather(String latitude, String longitude, int broj) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_ForecastDaily_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);
        webResource = webResource.queryParam("cnt", broj);

        //TODO složiti da ne puca ako nešto ne dođe
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        List<MeteoPodaci> listaMeteo = new ArrayList<>();
        try {
            JSONObject JOdgovor = new JSONObject(odgovor);
            JSONArray list = JOdgovor.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {

                JSONObject jo = list.getJSONObject(i);

                MeteoPodaci mp = new MeteoPodaci();

                mp.setTemperatureValue(Float.parseFloat(jo.getJSONObject("temp").getString("day")));
                mp.setTemperatureMin(Float.parseFloat(jo.getJSONObject("temp").getString("min")));
                mp.setTemperatureMax(Float.parseFloat(jo.getJSONObject("temp").getString("max")));
                mp.setTemperatureUnit("celsius");

                mp.setHumidityValue(Float.parseFloat(jo.getString("humidity")));
                mp.setHumidityUnit("%");

                mp.setPressureValue(Float.parseFloat(jo.getString("pressure")));
                mp.setPressureUnit(latitude);

                mp.setWindSpeedValue(Float.parseFloat(jo.getString("speed")));
                mp.setWindSpeedName("");

                mp.setWindDirectionValue(Float.parseFloat(jo.getString("deg")));
                mp.setWindDirectionCode("");
                mp.setWindDirectionName("");

                mp.setCloudsValue(jo.getInt("clouds"));
                mp.setCloudsName(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                mp.setPrecipitationMode("");

                mp.setWeatherNumber(jo.getJSONArray("weather").getJSONObject(0).getInt("id"));
                mp.setWeatherValue(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
                mp.setWeatherIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));

                mp.setLastUpdate(new Date(jo.getLong("dt")));
                listaMeteo.add(mp);

            }

            return listaMeteo;

        } catch (JSONException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
