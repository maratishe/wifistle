package com.example.wifistle;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.graphics.Typeface;

import android.widget.ToggleButton;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.widget.EditText;
import android.content.res.Resources;
import android.util.TypedValue;
import java.lang.reflect.Method;
import android.view.View;
import android.view.View.OnClickListener;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.widget.TextView;
import android.content.BroadcastReceiver; 
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import java.util.List;
import android.content.Intent;
import android.content.Context; 
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.lang.StringBuilder;
import java.lang.Exception;
import java.io.InputStreamReader; 
import java.net.URL; 

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.util.Hashtable;
import java.util.Set;
import java.util.Enumeration;
import java.util.ArrayList;
import android.util.Log;
import java.util.Locale;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.graphics.PorterDuff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Environment; 

import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import java.util.StringTokenizer;
import java.lang.Double; 

public class Speech implements TextToSpeech.OnInitListener {
	public String lang; 
	public String pitch; 
	public String text; 
	// speech recog
	Activity me;
	private SpeechRecognizer recog;
	private Intent intent;
	private int listenstate;
	private CountDownTimer timer; 
	private Runnable donec = null;
	private final int REQ_CODE_SPEECH_INPUT = 1;
	// text to speech
	private TextToSpeech tts;
	private int isrepeat; 
	public void onInit( int status) { if ( status == TextToSpeech.SUCCESS) { tts.setLanguage( string2locale( lang)); tts.setPitch( Float.valueOf( pitch)); }}
	//public void onUtteranceCompleted( String utteranceId) { again(); }
	protected class SpeechRecognitionListener implements RecognitionListener {
		public void onBeginningOfSpeech() {  }
		public void onBufferReceived(byte[] buffer) {  }
		public void onEndOfSpeech() { }
		public void onError( int error) { }
		public void onEvent( int eventType, Bundle params) {  }
		public void onPartialResults( Bundle partialResults) { }
		public void onReadyForSpeech( Bundle params) {  }
		public void onResults( Bundle results) { 
			ArrayList<String> result = results.getStringArrayList( SpeechRecognizer.RESULTS_RECOGNITION); 
			//area.setText( result.get( 0));
			text = result.get( 0); // store the results
			donec.run();  // notify the callback
		}
		public void onRmsChanged( float rmsdB) { }
	}
	public void init( Activity me2) { 
		me = me2; 
		tts = new TextToSpeech( me, this); 
		// speech
 		recog = SpeechRecognizer.createSpeechRecognizer( me);
    		intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    		intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    		intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, lang);
    		intent.putExtra( RecognizerIntent.EXTRA_CALLING_PACKAGE, me.getPackageName());
    		intent.putExtra( "android.speech.extra.DICTATION_MODE", true);
    		intent.putExtra( RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
    		SpeechRecognitionListener listener = new SpeechRecognitionListener();
    		recog.setRecognitionListener( listener);
	}
	// speech recog
	public void listen( Runnable donec2) { // call as  speech.listen( new Runnable() { public void run() { ... use speech.text as result;  }}) 
		donec = donec2; text = "";
		recog.startListening( intent);
		//timer = new CountDownTimer( 8000, 1000) {  public void onTick( long millisUntilFinished) { } public void onFinish() { }   };
     	}
     	public void speak( String text) { tts.speak( text, TextToSpeech.QUEUE_FLUSH, null); }
     	// utilities
     	private Locale string2locale( String s) {
     		String l = ""; String c = "";
     		StringTokenizer tempStringTokenizer = new StringTokenizer(s,"-");
     		if( tempStringTokenizer.hasMoreTokens()) l = ( String)tempStringTokenizer.nextElement();
     		if( tempStringTokenizer.hasMoreTokens()) c = ( String)tempStringTokenizer.nextElement();
     		return new Locale( l, c);
	}
	
}

