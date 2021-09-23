import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

/*
Сделать запрос к api https://api.latoken.com/v2/pair
Посчитать количество активных пар PAIR_STATUS_ACTIVE
Проверить что в массиве отсутствуют дубликаты торговых пар
Найти все пары у которых quantity tick менее 0.01
Вывести список cookie которые присылает сервер в ответе
 */

public class Api {

    final URL url = new URL("https://api.latoken.com/v2/pair");
    final HttpURLConnection con = (HttpURLConnection) url.openConnection();

    public Api() throws IOException {
    }

    public void validateResponse() throws IOException {
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        if(con.getResponseCode() != 200){
            System.out.println("ResponseCode not OK");
        }

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            String jsonString = content.toString();
            JSONArray arr = new JSONArray(jsonString);
            int countActive = 0;
            Map<Object, Object> pairs = new HashMap<>();
            Map<Object, Object> quantityTick = new HashMap<>();
            for (int i = 0; i < arr.length(); i++) {
                String status = arr.getJSONObject(i).getString("status");
                pairs.put(arr.getJSONObject(i).getString("baseCurrency"), arr.getJSONObject(i).getString("quoteCurrency"));
                if (status.contains("PAIR_STATUS_ACTIVE")) {
                    countActive = countActive + 1;
                }
                if (Double.parseDouble(arr.getJSONObject(i).getString("quantityTick")) < 0.01) {
                    quantityTick.put(arr.getJSONObject(i).getString("baseCurrency"), arr.getJSONObject(i).getString("quoteCurrency"));
                }
            }

            //Output
            System.out.println("Активные пары: " + countActive);
            Set<Object> duplicatesCheck = new HashSet<>(pairs.keySet());
            if (pairs.size() != duplicatesCheck.size()) {
                System.out.println("Дубликаты есть");
            } else {
                System.out.println("Дубликатов нет");
            }
            System.out.println("Все пары у которых quantity tick менее 0.01: " + quantityTick);

            Map<String, List<String>> headerFields = con.getHeaderFields();
            Set<String> headerFieldsSet = headerFields.keySet();
            for (String headerFieldKey : headerFieldsSet) {
                System.out.print(headerFieldKey + ":");
                List<String> headerFieldValue = headerFields.get(headerFieldKey);
                for (String headerValue : headerFieldValue) {
                    String[] fields = headerValue.split(";\s*");
                    String cookieValue = fields[0];
                    System.out.println(cookieValue);
                }
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}