/*
 Name:  Sameed Bhatti
 NetID: sab180010
 Date:  10-15-20

 Program Description:
 The program uses a Pircbot setup to give a specified report of two APIs.
 The first API is the weather API, which displays a detailed description of the weather/forecast of the given zip/city.
 The second API is the myAnimeList API, which displays a detailed description of the japaneseTv show named by the user.
 The myAnimeList API is one of the largest and most popular anime lists known, being an anime fan it was the first API I thought of creating.

 The program can be compiled and ran in the terminal using the following commands:
 javac -classpath pircbot.jar:gson-2.6.2.jar *.java
 java -classpath pircbot.jar:gson-2.6.2.jar Main.java
*/

import org.jibble.pircbot.*;
import java.net.http.*;
import java.net.URI;
import java.io.*;
import com.google.gson.*;

//Main class used to connect bot to main chat server.
public class Main {
    public static void main(String[] args) throws Exception {
        //Creating a hackBot object from the HackBot class, starts the bot up.
        HackBot hackBot = new HackBot( );

        //Using the hackBot object to call a function from the class which enables debugging output.
        hackBot.setVerbose(true);

        //Using the hackBot object to connect to an IRC server.
        //Go to website below and test the bot.
        hackBot.connect("irc.freenode.net");

        //#irchacks is the channel name that must be used on the server.
        hackBot.joinChannel("#irchacks");

        //Below are the initial statements outputted by the bot to display appropriate commands.
        //Prompting user to enter eligible hackBot commands.
        hackBot.sendMessage("#irchacks", "Hello, I am HackBot!");
        hackBot.sendMessage("#irchacks", "I can take the two following commands:");
        hackBot.sendMessage("#irchacks", "weather report <city> or japaneseTv search <name>");
        hackBot.sendMessage("#irchacks", "What would you like me to do?");
    }
}


//The WeatherCall class contains methods to call weather api, request data and parse Weather.json file
//Using method added in Java 11 instead of old school method
class WeatherCall{
    static File weatherJson;
    //Constructor
    public WeatherCall(){
        //input
        weatherJson = new File("Weather.json");
    }
    public static void main(String[] args){
    }

    //The ApiCall method is using the java.net.http.HTTPClient import Asynchronously to not block the main thread
    public static void ApiCall(String endPoint){
        //Creating a client, a HttpClient object
        HttpClient client = HttpClient.newHttpClient();

        //Creating a request, a HttpRequest object
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endPoint)).build();

        //Sending response using the client asynchronously
        //Using the ofString method to Tell server to send response as a String
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                //Apply this method on the response that was sent from the server
                .thenApply(HttpResponse::body)
                //Write the JSON data received to a file
                .thenApply(WeatherCall::writeResponse)
                //Returning the result
                .join();
    }

    //Method used by the client to write Response data to a file
    public static String writeResponse(String data){
        try{
            //creating a FileWriter object
            FileWriter writerObj = new FileWriter(weatherJson);
            writerObj.write(data);
            writerObj.close();
        }catch(IOException e){System.out.println(e);}
        finally { return ""; }
    }

    //Method used to parse data to a specific JSON Object
    public JsonObject getJsonObject(String name){
        try{
            //Creating a Json Parser
            JsonParser parser = new JsonParser();

            //Reading the data in the JSON file using an InputStream and reader
            InputStream inputStream = new FileInputStream("Weather.json");
            Reader reader = new InputStreamReader(inputStream);

            //Creating JSON elements until we arrive at the target (destination)
            JsonElement rootElement = parser.parse(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();
            //Arriving at the given target
            JsonObject target = rootObject.getAsJsonObject(name);

            return target;
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        return null;
    }

    //Method used to parse to a specific JSON array
    public JsonArray getJsonArray(String array){
        try{
            //Creating a Json Parser
            JsonParser parser = new JsonParser();

            //Reading the data in the JSON file using an InputStream and reader
            InputStream inputStream = new FileInputStream("Weather.json");
            Reader reader = new InputStreamReader(inputStream);

            //Creating JSON elements until we arrive at the target (destination)
            JsonElement rootElement = parser.parse(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();
            //Arriving at the given target
            JsonArray targetArray = rootObject.getAsJsonArray(array);

            return targetArray;
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        return null;

    }

    //Method used to parse to a specific JSON primitive
    public JsonPrimitive getJsonPrimitive(String primitive){
        try{
            //Creating a Json Parser
            JsonParser parser = new JsonParser();

            //Reading the data in the JSON file using an InputStream and reader
            InputStream inputStream = new FileInputStream("Weather.json");
            Reader reader = new InputStreamReader(inputStream);

            //Creating JSON elements until we arrive at the primitive (destination)
            JsonElement rootElement = parser.parse(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();
            //Arriving at the primitive
            JsonPrimitive targetPrimitive = rootObject.getAsJsonPrimitive(primitive);

            return targetPrimitive;
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        return null;
    }

    //Method used to delete Weather.json file
    public void deleteWeatherJson(){
        try{
            weatherJson.delete();
        }
        catch(Exception e){System.out.println(e);}
    }

    //Method used to get the root of the JSON File
    public JsonObject getRoot(){
        try{
            //Creating a Json Parser
            JsonParser parser = new JsonParser();

            //Reading the data in the JSON file using an InputStream and reader
            InputStream inputStream = new FileInputStream("Weather.json");
            Reader reader = new InputStreamReader(inputStream);

            //Creating JSON elements until we arrive at the primitive (destination)
            JsonElement rootElement = parser.parse(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();

            return rootObject;
        }
        catch(FileNotFoundException e){e.printStackTrace();}

        return null;
    }

    //Method used for input validation (city/zip)
    public boolean doesCityExist(String city){
        JsonObject rootObject = getRoot();

        if((rootObject.get("message") != null) && (rootObject.get("message").getAsString().equalsIgnoreCase("city not found"))){
            return false;
        }
        return true;
    }
}



 //JanpaneseTvCall - methods to call myanimelist api obtains JSON data of japanese tv shows (anime)
 //and parse JanTv.json file using GSON method
class JapaneseTvCall{
    //Making anime json file
    static File JapaneseTvFile;
    public JapaneseTvCall(){
        //File input anime json file
        JapaneseTvFile = new File("JanTv.json");
    }

    //Method using java.net.http.HTTPClient used Asynchronously
    //Asynchronously makes it so it will not block the main thread
     //Java 11 method
    public static void ApiCall(String endPoint){
        //Creating a client object from HttpClient class
        HttpClient client = HttpClient.newHttpClient();

        //Building a Request
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endPoint)).build();

        //Send response using the client asynchronously
        //".ofString" is used to tell the server to send response as a String
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                //Applying this method on the response that was sent from the server
                .thenApply(HttpResponse::body)
                //Writing the JSON data received to a file
                .thenApply(JapaneseTvCall::writeResponse)
                //Returning to our original result
                .join();
    }

    //Method to write Response data into a file (JanTv.json)
    public static String writeResponse(String data){
        try{
            FileWriter writer = new FileWriter("JanTv.json");
            writer.write(data);
            writer.close();
        }catch(IOException e){System.out.println(e);}
        finally{return "";}
    }

    //Method to parse to specific JSON array
    public JsonArray getJsonArray(String array){
        try{
            //Creating a Json Parser object
            JsonParser parserObject = new JsonParser();

            //Reading JSON file (JanTv) through creating inputStream and reader
            InputStream inputStream = new FileInputStream("JanTv.json");
            Reader reader = new InputStreamReader(inputStream);

            //Creating JSON elements until we get the number we need, destination (array)
            JsonElement rootElement = parserObject.parse(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();
            //Arriving at the array
            JsonArray targetArray = rootObject.getAsJsonArray(array);

            return targetArray;
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        return null;

    }

    //Method to delete JanTv.json file
    public void deleteJanTvJson(){
        try{
            JapaneseTvFile.delete();
        }
        catch(Exception e){System.out.println(e);}
    }
}




//Hackbot class reads responses from users and sends messages back to user
class HackBot extends PircBot{
    public HackBot(){
        this.setName("BhattiBot3");
    }


    //The method sends messages to the user
    //If the user requests the Weather report, it is given based on city/zip
    //If the user requests a Japanese Tv description, it is given based on the name
    public void onMessage(String channel, String sender, String login, String hostname, String message){

        message = message.toLowerCase();

        //Declaring response for the bot to send to user
        String botResponse = "";

        //Creating the weather call and japaneseTv call
        WeatherCall weatherCall = new WeatherCall();
        JapaneseTvCall japaneseTvCall = new JapaneseTvCall();

        //If user ask for the weather report the options are sent back to the user
        if(message.contains("weather") && message.contains("report") && (message.length() > 15)){
            //Retrieves the city
            String city = message.substring(15);

            //If the Weather.json file already exists the weatherCall is deleted
            if(weatherCall.weatherJson.exists()){
                //Delete Weather.json file so the switch works correctly
                weatherCall.deleteWeatherJson();
            }
            // Call the weather API to get specific JSON data of the user entered city
            try{
                weatherCall.ApiCall("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",usa&APPID=f958adb0d35c8e8b3a35e3c924570a49");
            }
            catch(Exception e){sendMessage(channel, sender + ": " + e.toString());}

            //User input validation for the for the city entered by user
            if(!(weatherCall.doesCityExist(city))){
                sendMessage(channel, sender + ": " + city + " is not a city in the US. Try again" );
            }
            else{
                //Options for the weather report
                sendMessage(channel, sender + ": What would you like to know about " + city + "?" );
                sendMessage(channel, sender + ": You can choose: coord, details, base, main, visibility, wind, clouds, dt, sys, timezone, id, name, cod." );
            }
        }
        else if(weatherCall.weatherJson.exists()){
            try{
                //Using switch statement to respond with the data requested
                switch(message){
                    //switch cases for the json objects
                    case "coord": {
                        JsonObject coord = weatherCall.getJsonObject("coord");
                        String longitude = coord.get("lon").getAsString();
                        String lattitude = coord.get("lat").getAsString();
                        botResponse = "The longitude is " + longitude + " and the lattitude is " + lattitude;
                    }break;
                    case "details": {
                        JsonArray weather = weatherCall.getJsonArray("weather");
                        JsonObject details = weather.get(0).getAsJsonObject();
                        String id = details.get("id").getAsString();
                        String main = details.get("main").getAsString();
                        String description = details.get("description").getAsString();
                        String icon = details.get("icon").getAsString();
                        botResponse = "The ID is " + id + ". " +
                                "The main is " + main + ". " +
                                "The description is " + description + ". " +
                                "The icon is " + icon + ". ";
                    }break;
                    case "main": {
                        JsonObject main = weatherCall.getJsonObject("main");
                        String temp = main.get("temp").getAsString();
                        String feels_like = main.get("feels_like").getAsString();
                        String temp_min = main.get("temp_min").getAsString();
                        String temp_max = main.get("temp_max").getAsString();
                        String pressure = main.get("pressure").getAsString();
                        String humidity = main.get("humidity").getAsString();
                        botResponse = "The temperaute is " + temp + " Kelvin. " +
                                "It feels like " + feels_like + " Kelvin. " +
                                "The minimum is " + temp_min + " Kelvin. " +
                                "The maximum is " + temp_max + " Kelvin. " +
                                "The pressure is " + pressure + " Pascals " +
                                "The humidity is " + humidity;

                    }break;
                    case "wind": {
                        JsonObject wind = weatherCall.getJsonObject("wind");
                        String speed = wind.get("speed").getAsString();
                        String degrees = wind.get("deg").getAsString();
                        botResponse = "The wind speed is " + speed + " mph. " +
                                "The wind angle is " + degrees + " degrees";
                    }break;
                    case "clouds": {
                        JsonObject clouds = weatherCall.getJsonObject("clouds");
                        String all = clouds.get("all").getAsString();
                        botResponse = "The all is " + all + ".";
                    }break;
                    case "sys": {
                        JsonObject sys = weatherCall.getJsonObject("sys");
                        String type = sys.get("type").getAsString();
                        String id = sys.get("id").getAsString();
                        String country = sys.get("country").getAsString();
                        String sunrise = sys.get("sunrise").getAsString();
                        String sunset = sys.get("sunset").getAsString();
                        botResponse = "The type is " + type + ". " +
                                "The ID is " + id + ". " +
                                "The country is " + country + ". " +
                                "The sunrise is " + sunrise + ". " +
                                "The sunset is " + sunset + ". ";
                    }break;
                    //switch cases for json primitives
                    case "base":{
                        String base = weatherCall.getJsonPrimitive("base").toString();
                        botResponse = "The base is " + base;
                    }break;
                    case "visibility":{
                        String visibility = weatherCall.getJsonPrimitive("visibility").toString();
                        botResponse = "The visibility is " + visibility;
                    }break;
                    case "dt":{
                        String dt = weatherCall.getJsonPrimitive("dt").toString();
                        botResponse = "The dt is " + dt;
                    }break;
                    case "timezone":{
                        String timezone = weatherCall.getJsonPrimitive("timezone").toString();
                        botResponse = "The timezone is " + timezone;
                    }break;
                    case "id":{
                        String id = weatherCall.getJsonPrimitive("id").toString();
                        botResponse = "The id is " + id;
                    }break;
                    case "name":{
                        String name = weatherCall.getJsonPrimitive("name").toString();
                        botResponse = "The name is " + name;
                    }break;
                    case "cod":{
                        String cod = weatherCall.getJsonPrimitive("cod").toString();
                        botResponse = "The cod is " + cod;
                    }break;
                    //default: {response = "Sorry, don't know that command";}
                }
                sendMessage(channel, sender + ": " + botResponse);
            }
            catch(Exception e){
                sendMessage(channel, sender + ": Please enter the city in the correct format (weather report <city>)");
                sendMessage(channel, sender + ": " + e.toString());
            }
        }

        //Results used in later if/else block declared here so that they can be global to both statements
        JsonArray results = new JsonArray();
        JsonObject result1 = new JsonObject();
        JsonObject result2 = new JsonObject();
        JsonObject result3 = new JsonObject();
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////was 13
        //japaneseTv search
        //If the user requests for the JanTv call, the following options are displayed
        //System.out.println(message);
        if(message.contains("japanesetv") && message.contains("search") && (message.length() > 18 )){
            // Retreive anime from message of format: "anime report <anime>"
            String anime = message.substring(18);
            //System.out.println(anime);

            //If the janTv.json file already exists then the japaneseTvCall is deleted
            if(japaneseTvCall.JapaneseTvFile.exists()){
                //JanTv.json file is deleted so the switch statement can work
                japaneseTvCall.deleteJanTvJson();
            }

            //Call the myanimelist API to get specific JSON data of the user entered japanese tv show
            try{
                japaneseTvCall.ApiCall("https://api.jikan.moe/v3/search/anime?q=" + anime);
                //System.out.println("aaaaaaaaaaaaa working");
            }
            catch(Exception e){sendMessage(channel, sender + ": " + e.toString());}

            //Retrieving the "results" array and declaring the JsonObjects for the top three results
            try{
                results = japaneseTvCall.getJsonArray("results");
                result1 = results.get(0).getAsJsonObject();
                result2 = results.get(1).getAsJsonObject();
                result3 = results.get(2).getAsJsonObject();

                //Display results to the user and ask them to choose which one they want to view
                sendMessage(channel, sender + ": Here were the results: ");
                sendMessage(channel, result1.get("title").getAsString());
                sendMessage(channel, result2.get("title").getAsString());
                sendMessage(channel, result3.get("title").getAsString());
                sendMessage(channel, "Which of the following japanese tv shows would you like to view?");
            }
            catch(Exception e){sendMessage(channel, sender + ": " + "No results");}
        }
        else if(japaneseTvCall.JapaneseTvFile.exists()){
            //Loading in the results
            results = japaneseTvCall.getJsonArray("results");
            result1 = results.get(0).getAsJsonObject();
            result2 = results.get(1).getAsJsonObject();
            result3 = results.get(2).getAsJsonObject();

            //Checking which tv show the user picked and displaying the correct information
            if(message.equalsIgnoreCase(result1.get("title").getAsString())){
                sendMessage(channel, "Title: " + result1.get("title").getAsString());
                sendMessage(channel, "Airing: " + result1.get("airing").getAsString());
                sendMessage(channel, "Synopsis: " + result1.get("synopsis").getAsString());
                sendMessage(channel, "Type: " + result1.get("type").getAsString());
                sendMessage(channel, "Episodes: " + result1.get("episodes").getAsString());
                sendMessage(channel, "Start Date: " + result1.get("start_date").getAsString().substring(0, 10));
                sendMessage(channel, "End Date: " + result1.get("end_date").getAsString().substring(0, 10));
                sendMessage(channel, "Rating out of 10: " + result1.get("score").getAsString());
            }
            if(message.equalsIgnoreCase(result2.get("title").getAsString())){
                sendMessage(channel, "Title: " + result2.get("title").getAsString());
                sendMessage(channel, "Airing: " + result2.get("airing").getAsString());
                sendMessage(channel, "Synopsis: " + result2.get("synopsis").getAsString());
                sendMessage(channel, "Type: " + result2.get("type").getAsString());
                sendMessage(channel, "Episodes: " + result2.get("episodes").getAsString());
                sendMessage(channel, "Start Date: " + result2.get("start_date").getAsString().substring(0, 10));
                sendMessage(channel, "End Date: " + result2.get("end_date").getAsString().substring(0, 10));
                sendMessage(channel, "Rating out of 10: " + result2.get("score").getAsString());
            }
            if(message.equalsIgnoreCase(result3.get("title").getAsString())){
                sendMessage(channel, "Title: " + result3.get("title").getAsString());
                sendMessage(channel, "Airing: " + result3.get("airing").getAsString());
                sendMessage(channel, "Synopsis: " + result3.get("synopsis").getAsString());
                sendMessage(channel, "Type: " + result3.get("type").getAsString());
                sendMessage(channel, "Episodes: " + result3.get("episodes").getAsString());
                sendMessage(channel, "Start Date: " + result3.get("start_date").getAsString().substring(0, 10));
                sendMessage(channel, "End Date: " + result3.get("end_date").getAsString().substring(0, 10));
                sendMessage(channel, "Rating out of 10: " + result3.get("score").getAsString());
            }
        }
    }
}
