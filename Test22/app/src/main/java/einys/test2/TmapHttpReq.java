package einys.test2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kim Yangsun on 2017-06-11.
 * Tmap Http API 유저 인증을 하고 쿼리 전송을 하는 클래스입니다.
 */

public class TmapHttpReq {

    private HttpURLConnection conn = null;
    private String myAppKey = "6c5dc4df-49da-32f6-ad3b-4bf375e36ce9";
    private StringBuilder sb = new StringBuilder();
    URL url = null;


    public String post(final String urlStr){
        try {
            URL url = new URL(urlStr); //요청 URL을 입력
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
            conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
            conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
            conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)

            conn.setRequestProperty("appKey", myAppKey);

            new Thread(){
                public void run(){

                    try {


                        OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                        bw.write(""); // 매개변수 전송
                        bw.flush();
                        bw.close();
                        os.close();

                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(line);
                        }

                        System.out.println("response:" + sb.toString());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return sb.toString();

    }

    /*get으로 할걸 post로 하면 FileNotFound 에러가 발생한다*/
    public String get(final String urlStr){
        try {
            URL url = new URL(urlStr); //요청 URL을 입력
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); //요청 방식을 설정 (default : GET)
            conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
            //conn.setDoOutput(true); //output을 사용하도록 설정 (default : false) //OutPut을 사용하면 자동으로 POST가 된다.
            conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)

            conn.setRequestProperty("appKey", myAppKey);

            new Thread(){
                public void run(){

                    try {

                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(line);
                        }

                        System.out.println("response:" + sb.toString());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return sb.toString();

    }
}
