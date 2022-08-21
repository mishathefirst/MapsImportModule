package com.example.demo;

import com.example.demo.entities.Coordinates;
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


    @GetMapping("")
    public ResponseEntity<StringBuilder> getListOfNearbyInfrastructure(@RequestParam("address") String address) {
        StringBuilder content = new StringBuilder();

        //query 1
        //getting object's coordinates
        String locationData = runFirstQuery(address, content);


        //query 2
        //getting access zones
        String geometryCoordinates = runSecondQuery(address, locationData, content);



        //query 3
        //getting objects in the corresponding access zones
        runThirdQuery(locationData, content, geometryCoordinates);

        return new ResponseEntity<>(content, HttpStatus.OK);

    }

    private String runFirstQuery(String address, StringBuilder content) {
        final String QUERY1_BASIC_URL = "https://demo.maps.mail.ru/v3/search?api_key=demo&q=";
        try {
            URL url = new URL(QUERY1_BASIC_URL + address);
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


        return locationData;
    }

    private String runSecondQuery(String address, String locationData, StringBuilder content) {

        final String QUERY2BASICURL = "https://demo.maps.mail.ru/v2/iso?api_key=demo";
        StringBuilder geometryCoordinates = new StringBuilder();

        try{
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(QUERY2BASICURL);

            String[] coordinates = locationData.split(",");


            //change costing
            String jsonOutputString = "{\"locations\":[{\"lat\":" + coordinates[0] + ",\"lon\":" + coordinates[1] + "}],\"costing\":\"pedestrian\",\"contours\":[{\"time\":15}]}";

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            StringEntity stringEntity = new StringEntity(jsonOutputString);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            byte[] contentBytes;
            String contentStr;
            if (entity != null) {
                InputStream instream = entity.getContent();
                contentBytes = new byte[(int) entity.getContentLength()];
                instream.read(contentBytes, 0, (int) entity.getContentLength());
                contentStr = new String(contentBytes, StandardCharsets.UTF_8);
                System.out.println("_____");
                System.out.println(contentStr);
                System.out.println("_____");

                geometryCoordinates = new StringBuilder(contentStr.substring(contentStr.indexOf("coordinates") + 14, contentStr.indexOf("],\"type\"")));

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
        //add coordinates to mainObject


        content.delete(0, content.length());



        //timeout 2
        long startNew = System.currentTimeMillis();
        long endNew = startNew + 20 * 1000;
        int someNumberNew = 0;
        while (System.currentTimeMillis() < endNew) {
            someNumberNew += 1;
        }
        System.out.println(geometryCoordinates.toString());
        return geometryCoordinates.toString();
    }

    private void runThirdQuery(String locationData, StringBuilder content, String geometryCoordinates) {

        final String QUERY3BASICURL = "https://demo.maps.mail.ru/v3/places?api_key=demo&q=";

        //change query params in the string
        try {
            //implement objects variety
            String objectType = "кафе";

            String[] coordinates = locationData.split(",");

            URL url = new URL(QUERY3BASICURL + objectType + "&location=" + coordinates[1] + "," + coordinates[0] + "&radius=" + convertCoordinatesToRadius(locationData, geometryCoordinates));
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
    }



    //approximate distance based on coordinates
    public double convertCoordinatesToRadius (String locationData, String geometryCoordinates) {

        //approx distance on Earth of one degree
        final int ANGLE_DISTANCE = 111139;

        double distance;
        double[] distances = new double[4], distanceLong = new double[4], distanceLat = new double[4];
        String[] basicCoordinatesString = locationData.split(",");
        double[] basicCoordinates = new double[2];
        basicCoordinates[0] = Double.parseDouble(basicCoordinatesString[1]);
        basicCoordinates[1] = Double.parseDouble(basicCoordinatesString[0]);

        String[] rangeCoordinatesString = geometryCoordinates.split("],");
        Coordinates[] rangeCoordinates = new Coordinates[4];
        for (int i = 0; i < 4; i++) {
            String[] localCoordinatesArray = rangeCoordinatesString[i].split(",");
            localCoordinatesArray[0] = localCoordinatesArray[0].substring(1, localCoordinatesArray[0].length() + 1);

            //TODO: check the correctness of the coordinates order
            rangeCoordinates[i].setLongitude(Double.parseDouble(localCoordinatesArray[0]));
            rangeCoordinates[i].setLatitude(Double.parseDouble(localCoordinatesArray[1]));
        }



        for (int i = 0; i < 4; i++) {
            distances[i] = Math.sqrt(Math.pow(distanceLong[i], 2) + Math.pow(distanceLat[i], 2));
        }
        distance = (distances[0] + distances[1] + distances[2] + distances[3]) / 4;
        return distance;
    }

}

