package org.talend.demo.todo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Todo {

    public final static String TODO_LIST_CATEG = "/categ/list";
    public final static String TODO_FILL_DATA = "/fill";
    public final static String TODO_RESET = "/reset";
    public final static String TODO_ADD = "/todo/add";

    public final static String TODO_LIST_ALL = "/list";

    public final static String EXPECTED_TOKEN = "Bearer 1234567";
    public final static String Authorization_header = "Authorization";

    private static Map<String, List<Entry>> data = new HashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();

        System.out.println("********************* START SERVER on port " + port);

        configureServer(server);

        server.start();
    }

    private static void configureServer(HttpServer server) {
        listCategs(server);
        fillData(server);
        reset(server);
        addEntry(server);
        list(server);
    }

    private static void listCategs(HttpServer server) {
        server.createContext(TODO_LIST_CATEG, new HttpHandler() {

            public void handle(HttpExchange exchange) throws IOException {
                Set<String> categs = data.keySet();
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                categs.stream().forEach(c -> {
                    JsonObject categ = Json.createObjectBuilder().add("name", c).build();
                    arrayBuilder.add(categ);
                });

                int status = HttpURLConnection.HTTP_OK;
                byte[] payload = arrayBuilder.build().toString().getBytes();
                sendJson(status, payload, exchange);
            }
        });
    }

    private static void list(HttpServer server) {
        server.createContext(TODO_LIST_ALL, new HttpHandler() {

            public void handle(HttpExchange exchange) throws IOException {
                JsonObject all = _listAll();

                int status = HttpURLConnection.HTTP_OK;
                byte[] payload = all.toString().getBytes();
                sendJson(status, payload, exchange);
            }
        });
    }


    private static void addEntry(HttpServer server) {
        server.createContext(TODO_ADD, new HttpHandler() {

            public void handle(HttpExchange exchange) throws IOException {
                if(!_checkAuth(exchange)){
                    return;
                }

                JsonReader reader = Json.createReader(exchange.getRequestBody());
                JsonObject jsonObject = reader.read().asJsonObject();
                Entry todo = new Entry(false, jsonObject.getString("todo"));
                String categ = jsonObject.getString("categ").toLowerCase();
                if(!data.containsKey(categ)){
                    data.put(categ, new ArrayList<>());
                }
                data.get(categ).add(todo);

                JsonObject all = _listAll();

                int status = HttpURLConnection.HTTP_OK;
                byte[] payload = all.toString().getBytes();
                sendJson(status, payload, exchange);
            }
        });
    }

    private static void fillData(HttpServer server) {
        server.createContext(TODO_FILL_DATA, new HttpHandler() {

            public void handle(HttpExchange exchange) throws IOException {
                _fillData();

                JsonObject response = Json.createObjectBuilder().add("response", "OK").build();
                int status = HttpURLConnection.HTTP_OK;
                byte[] payload = response.toString().getBytes();
                sendJson(status, payload, exchange);
            }
        });
    }

    private static void reset(HttpServer server) {
        server.createContext(TODO_RESET, new HttpHandler() {

            public void handle(HttpExchange exchange) throws IOException {
                _reset();

                JsonObject response = Json.createObjectBuilder().add("response", "OK").build();
                int status = HttpURLConnection.HTTP_OK;
                byte[] payload = response.toString().getBytes();
                sendJson(status, payload, exchange);
            }
        });
    }

    private static boolean _checkAuth(HttpExchange exchange) throws IOException {
        List<String> authorization = exchange.getRequestHeaders().get(Authorization_header);
        if(authorization == null || authorization.size() <= 0 || !EXPECTED_TOKEN.equals(authorization.get(0))){
            JsonObject response = Json.createObjectBuilder().add("response", "Bad authentication").build();
            int status = HttpURLConnection.HTTP_UNAUTHORIZED;
            byte[] payload = response.toString().getBytes();
            sendJson(status, payload, exchange);
            return false;
        }
        return true;
    }

    private static void _reset(){
        data.clear();
    }

    private static JsonObject _listAll(){
        JsonObjectBuilder categs = Json.createObjectBuilder();
        for(String c : data.keySet()){
            JsonArrayBuilder content = Json.createArrayBuilder();
            for(Entry t : data.get(c)){
                JsonObjectBuilder entry = Json.createObjectBuilder();
                entry.add("done", t.isDone());
                entry.add("todo", t.getTodo());
                content.add(entry.build());
            }
            categs.add(c, content.build());
        }

        return categs.build();
    }

    private static void _fillData(){
        List<Entry> prez = new ArrayList<>();
        prez.add(new Entry(false, "Create TODO project"));
        prez.add(new Entry(false, "Create PPT"));
        prez.add(new Entry(false, "Do repeat myself."));
        data.put("jnsq", prez);

        List<Entry> we = new ArrayList<>();
        we.add(new Entry(false, "Clean house"));
        we.add(new Entry(false, "Cook for week"));
        we.add(new Entry(false, "Have a good rest."));
        data.put("week-end", we);

        List<Entry> training = new ArrayList<>();
        training.add(new Entry(false, "Run 10km"));
        training.add(new Entry(false, "Run 15km"));
        training.add(new Entry(false, "Semi marathon inscription."));
        data.put("training", training);
    }

    private static String getRequestBodyAsString(HttpExchange exchange){
            return new Scanner(exchange.getRequestBody(), "UTF-8").useDelimiter("\\A").next();
    }

    private static void sendJson(int status, byte[] json, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, json.length);
        OutputStream os = exchange.getResponseBody();
        os.write(json);
        os.close();
    }

    @Data
    @AllArgsConstructor
    public static class Entry{
        private boolean done;
        private String todo;
    }

}
