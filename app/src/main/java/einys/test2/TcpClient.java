package einys.test2;

/**
 * Created by Ysunny on 2017-06-13.
 */

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


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

                Log.e("Socket", "Trying to Connect...1");

                socket = new Socket(ip, port);

                Log.e("Socket", "Connected...2 신호등이 가까이 있습니다.");
                checkUpdate.start();


                String message = "START";
                try {
                    Log.i("message", message);

                    //두 번째 인자 True면 자동
                    PrintWriter out = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(
                                    socket.getOutputStream())), true);
                    out.println(message);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    /*
                    //닫기. Socket
                    socket.close();
                    System.out.println("Client:Socket closed");
                    */
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
                    showUpdate.run();
                }
            } catch (Exception e) {

            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            Log.i("From server", html);
        };
    };


    //Constructor***
    public TcpClient(String ipStr, int port) {

        this.ip = ipStr;
        this.port = port;
        mHandler = new Handler();
        Client.start();


    }

    public void sendSTART(){

        if(socket.isConnected()==true) {
            PrintWriter out = null;
            try {

                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(
                                socket.getOutputStream())), true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("START");
            out.flush();
        }
    }

    public void sendSTOP(){

        if(socket.isConnected()==true) {

            PrintWriter out = null;
            try {
                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(
                                socket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("STOP");
            out.flush();
        }
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


