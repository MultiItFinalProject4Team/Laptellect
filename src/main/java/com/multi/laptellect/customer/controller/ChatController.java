package com.multi.laptellect.customer.controller;


import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;


@Controller
public class ChatController {

    @Value("${spring.ncp.chatbot.key}")
    private String secretKey;
    @Value("${spring.ncp.chatbot.url}")
    private String apiUrl;

    /**
     * 소켓통신을 활용한 챗봇 사용 메소드
     *
     * @param roomId
     * @param chatMessage
     * @return 전처리를 완료한 메시지
     * @throws IOException
     */
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/public/{roomId}")
    public String sendMessage(@DestinationVariable("roomId") String roomId, @Payload String chatMessage) throws IOException
    {

        URL url = new URL(apiUrl);

        String message =  getReqMessage(chatMessage);
        String encodeBase64String = makeSignature(message, secretKey);

        //api서버 접속 (서버 -> 서버 통신)
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;UTF-8");
        con.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        wr.write(message.getBytes("UTF-8"));
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();

        BufferedReader br;

        if(responseCode==200) { // 정상 호출

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            con.getInputStream(), "UTF-8"));
            String decodedString;
            String jsonString = "";
            while ((decodedString = in.readLine()) != null) {
                jsonString = decodedString;
                System.out.println(jsonString);
            }

            //받아온 값을 세팅하는 부분
            JSONParser jsonparser = new JSONParser();
            try {
                JSONObject json = (JSONObject)jsonparser.parse(jsonString);
                JSONArray bubblesArray = (JSONArray)json.get("bubbles");
                JSONObject bubbles = (JSONObject)bubblesArray.get(0);
                String type = (String)bubbles.get("type");
                if(type.equals("template")) {
                    JSONObject data = (JSONObject) bubbles.get("data");
                    JSONObject data2 = (JSONObject) data.get("cover");
                    JSONObject data3 = (JSONObject) data2.get("data");
                    String description = "";
                    description = (String) data3.get("description");
                    chatMessage = description;
                    JSONArray contentTable = (JSONArray) data.get("contentTable");
                    String link_url = getString(contentTable);
                    chatMessage+="\n 이동하기#"+link_url;
                    System.out.println("테스트"+(link_url));
                }else if(type.equals("text")){
                    JSONObject data = (JSONObject) bubbles.get("data");
                    String description = "";
                    description = (String) data.get("description");
                    chatMessage = description;
                }
                System.out.println(chatMessage);
            } catch (Exception e) {
                System.out.println("error");
                e.printStackTrace();
            }

            in.close();
        } else {  // 에러 발생
            chatMessage = con.getResponseMessage();
        }
        return chatMessage;
    }

    /**
     * 링크 형태의 답변에서 링크를 추출하는 메소드
     *
     * @param contentTable
     * @return 링크 url
     */
    private static String getString(JSONArray contentTable) {
        JSONArray linkArray = (JSONArray) contentTable.get(0);
        JSONObject link_data = (JSONObject)linkArray.get(0);
        JSONObject link_data2 = (JSONObject)link_data.get("data");
        JSONObject link_data3 = (JSONObject)link_data2.get("data");
        JSONObject link_data4 = (JSONObject)link_data3.get("action");
        JSONObject link_data5 = (JSONObject)link_data4.get("data");
        String link_url = (String)link_data5.get("url");
        return link_url;
    }

    /**
     * 보낼 메세지를 네이버에서 제공해준 암호화로 변경해주는 메소드
     *
     * @param message
     * @param secretKey
     * @return 암호화 된 보낼 메시지
     */
    public static String makeSignature(String message, String secretKey) {

        String encodeBase64String = "";

        try {
            byte[] secrete_key_bytes = secretKey.getBytes("UTF-8");

            SecretKeySpec signingKey = new SecretKeySpec(secrete_key_bytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;

        } catch (Exception e){
            System.out.println(e);
        }

        return encodeBase64String;

    }

    /**
     * 보낼 메세지를 네이버 챗봇 포맷으로 변경해주는 메소드
     *
     * @param voiceMessage
     * @return 보낼 메시지
     */
    public static String getReqMessage(String voiceMessage) {

        String requestBody = "";

        try {

            JSONObject obj = new JSONObject();

            long timestamp = new Date().getTime();

            System.out.println("##"+timestamp);

            obj.put("version", "v2");
            obj.put("userId", "customer"+UUID.randomUUID());
            obj.put("timestamp", timestamp);

            JSONObject bubbles_obj = new JSONObject();

            bubbles_obj.put("type", "text");

            JSONObject data_obj = new JSONObject();
            data_obj.put("description", voiceMessage);

            bubbles_obj.put("type", "text");
            bubbles_obj.put("data", data_obj);

            JSONArray bubbles_array = new JSONArray();
            bubbles_array.add(bubbles_obj);

            obj.put("bubbles", bubbles_array);
            obj.put("event", "send");

            requestBody = obj.toString();

        } catch (Exception e){
            System.out.println("## Exception : " + e);
        }

        return requestBody;

    }
}