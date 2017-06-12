package einys.test2;

/**
 * Created by ajou on 2017-06-12.
 */


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TcpClient {

    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ip = "xxx.xxx.xxx.xxx"; // IP
    private int port = 9999; // PORT번호

    public void setSocket(String ip, int port) throws IOException {

        try {
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

    private Thread Client = new Thread() {

        public void run() {

            try {
                System.out.println("Client: Connecting");
                //IP 주소 및 포트 번호 (대응 서비스 사이드) 이 IP 본지 路由器 IP 주소
                Socket socket = new Socket("192.168.0.14", 8001);
                //보내면 서비스 사이드 소식
                String message = "Message from Android phone";
                try {
                    System.out.println("Client Sending: '" + message + "'");

                    //두 번째 인자 True 侧视 자동 얼굴이 붉어지다
                    PrintWriter out = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(
                                    socket.getOutputStream())), true);
                    out.println(message);
//                      out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //닫기. Socket
                    socket.close();
                    System.out.println("Client:Socket closed");
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



/*
            try {
                setSocket(ip, port);
                checkUpdate.start();


                OutputStream os = socket.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                OutputStreamWriter out = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(out); // UTF-8로 전송
                bw.write(""); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();

                //PrintWriter out = new PrintWriter(networkWriter, true);
                String return_msg = "START";
                bw.write(return_msg);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            */
        }
    };


    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                String line;
                Log.w("ChattingStart", "Start Thread");
                while (true) {
                    Log.w("Chatting is running", "chatting is running");
                    line = networkReader.readLine();
                    html = line;
                    mHandler.post(showUpdate);
                }
            } catch (Exception e) {

            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            Log.i("From server", html);
        }

    };

    public TcpClient(String ipStr, int port) {
        mHandler = new Handler();
        Client.start();

    }

}




    /*
    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        try {
            setSocket(ip, port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        checkUpdate.start();

        PrintWriter out = new PrintWriter(networkWriter, true);
        String return_msg = "helloWorld";
        out.println(return_msg);

    }

    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                String line;
                Log.w("ChattingStart", "Start Thread");
                while (true) {
                    Log.w("Chatting is running", "chatting is running");
                    line = networkReader.readLine();
                    html = line;
                    mHandler.post(showUpdate);
                }
            } catch (Exception e) {

            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            Toast.makeText(TcpClient.this, "Coming word: " + html, Toast.LENGTH_SHORT).show();
        }

    };

    public void setSocket(String ip, int port) throws IOException {

        try {
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }
    */


