package team4.blindhelper_test4;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

    public class MainActivity extends Activity implements OnInitListener{

        private TextToSpeech myTTS;
        private TextView txtSpeechInput;
        private ImageButton btnSpeak;
        private final int REQ_CODE_SPEECH_INPUT = 100;
        private String DEST; // STT로 변환한 TEXT값


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTTS = new TextToSpeech(this, this);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);


            btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
            btnSpeak.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //SpeechToText stt = new SpeechToText();
                    promptSpeechInput();
                }

            });




    }

    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch(InterruptedException e){}

    }

    public void onInit(int status){
        String start = "아래 버튼을 눌러 시작해주세요";
        myTTS.speak(start, TextToSpeech.QUEUE_ADD, null);
    }


    @Override
    protected void onDestroy() { // tts flush기능
        super.onDestroy();
        myTTS.shutdown();
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    DEST = result.get(0);
                    //myTTS.speak(DEST+"가 맞습니까", TextToSpeech.QUEUE_ADD, null); // 주소 되물어보기
                }
                break;
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

