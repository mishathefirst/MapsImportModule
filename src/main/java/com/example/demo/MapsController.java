package com.example.demo;

import com.example.demo.entities.MainObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

import java.nio.charset.StandardCharsets;



@Controller
public class MapsController {

    private static String Query1BasicURL = "https://demo.maps.mail.ru/v3/search?api_key=demo&q=";

    @GetMapping("")
    public ResponseEntity<StringBuilder> getListOfNearbyInfrastructure(@RequestParam("address") String address) {


        StringBuilder content = new StringBuilder();
        //query 1
        try {
            URL url = new URL(Query1BasicURL + address);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int locationDataStart = content.indexOf("pin");
        String locationData = content.toString().substring(locationDataStart + 15, locationDataStart + 24)
                .concat(",").concat(content.toString().substring(locationDataStart + 33, locationDataStart + 42));
        content.delete(0, content.length());


        //В locationData - корректные координаты объекта через запятую без пробела
        System.out.println("_____");
        System.out.println(locationData);
        System.out.println("_____");


        //timeout 1
        //caused by free version of the maps platform limitations
        long start = System.currentTimeMillis();
        long end = start + 20 * 1000;
        int someNumber = 0;
        while (System.currentTimeMillis() < end) {
            someNumber += 1;
        }



        //query 2
        try{
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://demo.maps.mail.ru/v2/iso?api_key=demo");

            String[] coordinates = locationData.split(",");
            String jsonOutputString = "{\"locations\":[{\"lat\":" + coordinates[0] + ",\"lon\":" + coordinates[1] + "}],\"costing\":\"pedestrian\",\"contours\":[{\"time\":15}]}";

            //List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            //params.add(new BasicNameValuePair("Accept", "application/json"));
            //params.add(new BasicNameValuePair("Content-Type", "application/json"));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            //httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            StringEntity stringEntity = new StringEntity(jsonOutputString);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            byte[] contentBytes;
            if (entity != null) {
                    InputStream instream = entity.getContent();
                    contentBytes = new byte[(int) entity.getContentLength()];
                    instream.read(contentBytes, 0, (int) entity.getContentLength());
                    String contentStr = new String(contentBytes, StandardCharsets.UTF_8);
                    System.out.println("_____");
                    System.out.println(contentStr);
                    System.out.println("_____");
                    instream.close();
                }
            httpPost.abort();
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        String[] locationArray = new String[2];
        locationArray = locationData.split(",");
        MainObject mainObject = new MainObject(address, Float.parseFloat(locationArray[0]), Float.parseFloat(locationArray[1]));


        content.delete(0, content.length());



        //timeout 2
        long startNew = System.currentTimeMillis();
        long endNew = startNew + 20 * 1000;
        int someNumberNew = 0;
        while (System.currentTimeMillis() < endNew) {
            someNumberNew += 1;
        }




        //change query params in the string
        //query 3
        try {
            URL url = new URL("https://demo.maps.mail.ru/v3/places?api_key=demo&q=кафе&location=59.936258,30.318024&radius=500");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("_____");
        System.out.println(content);
        System.out.println("_____");

        return new ResponseEntity<>(content, HttpStatus.OK);

    }



    public int convertCoordinatesToRadius () {
        
    }

}

