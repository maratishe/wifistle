package com.example.wifistle;
import com.example.wifistle.R;

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
import android.util.Log;

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

import android.os.CountDownTimer;

public class Wifistle extends Activity {
	private VBoxes boxes;
	private ToggleButton toggle;
	private EditText input1;
	private EditText input2;
	private TextView area;
	private Thread thread;
	private int at; 
	private int now; 
	private long before;
	private CountDownTimer timer; 
	private String BURL = "10.97.17.233:8001";
	private String NAME = "node1";
	private String mode = "off"; // on: regular API calls,  off: no API calls
	public WebGet web;
	public void onCreate( Bundle savedInstanceState) {
		super.onCreate( savedInstanceState); 
		boxes = new VBoxes( this, Color.BLACK); setContentView( boxes.getbody());
		toggle = boxes.toggle( "RUNNING...", "START", false, true, false); boxes.params( toggle, 1.0, 0.1, true); toggle.setOnCheckedChangeListener( new ToggleButton.OnCheckedChangeListener() { public void onCheckedChanged( CompoundButton b, boolean state) {  
			if ( state) { mode = "on"; top(); }
			else { mode = "off"; timer.cancel(); }
		}});    
		input1 = boxes.input( Color.BLUE, Color.WHITE, NAME, true, true); boxes.params( input1, 1.0, 0.1, true); 
		input2 = boxes.input( Color.BLUE, Color.WHITE, BURL, true, true); boxes.params( input2, 1.0, 0.1, true);
		area = boxes.text( Color.WHITE, true, true);
		area.setText( "click the toggle button to go into the ON mode");
	}
	public void top() { timer = new CountDownTimer( 3000, 1000) {  public void onTick( long millisUntilFinished) { }; public void onFinish() { 
		if ( mode == "off") return; callbase(); 
	}}.start(); }
	public void callbase() { 
		web = new WebGet(); web.BURL = "http://" + input2.getText().toString() + "/api.php";
		JSONObject json = web.call( "action=istask&one=" + input1.getText().toString());
		if ( json == null) { area.setText( area.getText() + "\n" + "ERROR! NULL json returned"); top(); return; }
		area.setText( json.toString());
		if ( ! web.has( "task")) { area.setText( area.getText() + "\n" + "no 'task', sleep..."); timer.start(); return; }
		now = Integer.parseInt( web.get( "now")); at = Integer.parseInt( web.get( "at")); 
		area.setText( web.get( "task") + " at " + at + "ms from now " + now + " (diff:" + ( at - now) + ")");
		if ( at - now <= 1000) at = now + 1000; 
		CountDownTimer timer2 = new CountDownTimer( at - now, 200) {  public void onTick( long millisUntilFinished) { }; public void onFinish() { 
			if ( web.get( "task").equals( "hear")) hear( web); // filename, duration (ms)
			if ( web.get( "task").equals( "hearspec")) hearspec( web); // filename, duration (ms)
			if ( web.get( "task").equals( "whistle")) whistle( web); // hz, duration (ms) 
			if ( web.get( "task").equals( "wifi")) wifi( web); // filename  -- dumps list of WiFi APs to a file
			if ( web.get( "task").equals( "xyz")) xyz( web); // filename, duration (ms)
			if ( web.get( "task").equals( "speech")) speech( web); // lang, pitch
		}}.start();
		
	}
	public void hear( WebGet web) { // filename, duration (ms)
		area.setText( "running Hear");
		final Hear hear = new Hear(); hear.filepath = "/sdcard/" + web.get( "filename");
		CountDownTimer timer2 = new CountDownTimer( Integer.parseInt( web.get( "duration")), 200) {  public void onTick( long millisUntilFinished) { }; public void onFinish() { hear.stop(); timer.start(); }}.start();
		hear.start(); area.setText( area.getText() + "\n" + "Started Hear");
	}
	public void hearspec( WebGet web) { // filename, duration (ms), low(hz), high(hz)  -- duration is used within the function
		area.setText( "running Hearspec (spectrum)");
		final Spectrum spectrum = new Spectrum(); spectrum.filepath = "/sdcard/" + web.get( "filename"); spectrum.low = Integer.parseInt( web.get( "low")); spectrum.high = Integer.parseInt( web.get( "high"));
		spectrum.duration = Integer.parseInt( web.get( "duration")); if ( web.has( "eachpage")) spectrum.dumpEachPage = true; // will dump each page now
		spectrum.start(); area.setText( area.getText() + "\n" + "Started Spectrum");
		CountDownTimer timer2 = new CountDownTimer( spectrum.duration + 1000, 200) {  public void onTick( long millisUntilFinished) { }; public void onFinish() {  spectrum.stop(); spectrum.dump(); timer.start();  }}.start();
	}
	public void whistle( WebGet web) { // hzs, loudness, duration (ms)
		area.setText( "running Whistle"); int duration = Integer.parseInt( web.get( "duration")); 
		final Whistle w = new Whistle(); w.duration = duration; w.freqlist = web.get( "hzs"); w.loudness = ( double)Float.valueOf( web.get( "loudness")); 
		area.setText( area.getText() + "\n" + "freqlist: " + w.freqlist);
		w.prepare(); area.setText( area.getText() + "\n" + "level=" + w.level + " freqs[" + w.freqs[ 0] + "...]");
		before = System.currentTimeMillis();
		final CountDownTimer timer2 = new CountDownTimer( duration + 3000, 500) {  public void onTick( long millisUntilFinished) { }; public void onFinish() { area.setText( area.getText() + "\n" + "Callback TIMEOUT!"); timer.start(); }}.start();
		w.run( new Runnable() { public void run() { area.setText( area.getText() + "\n" + "Callback called (took " + ( System.currentTimeMillis() - before) + "ms)"); timer2.cancel(); timer.cancel(); timer.start();  }}); 
		area.setText( area.getText() + "\n" + "w.run() for " + duration + "ms");
	}
	public void wifi( WebGet web) { // filename
		area.setText( "running WiFi");
		final WiFi w = new WiFi(); w.filepath = "/sdcard/" + web.get( "filename"); w.init( this); 
		w.listen( w.filepath); 
		w.scan(); 
		w.stop();
		area.setText( area.getText() + "\n" + "scan done, see file for details");
		timer.start();
	}
	public void xyz( WebGet web) { // filename, duration (ms)
		area.setText( "running ZYZ");
		final XYZ w = new XYZ(); w.filepath = "/sdcard/" + web.get( "filename"); w.init( this);
		CountDownTimer timer2 = new CountDownTimer( Integer.parseInt( web.get( "duration")), 200) {  public void onTick( long millisUntilFinished) { }; public void onFinish() { area.setText( area.getText() + "\n" + "stopped XYZ"); w.stop(); timer.start(); }}.start();
		w.start(); area.setText( area.getText() + "\n" + "Started XYZ");
	}
	public void speech( WebGet web) { // lang, pitch
		area.setText( "running speech");
		final Speech w = new Speech(); w.init( this); w.lang = web.get( "lang"); w.pitch = web.get( "pitch"); 
		w.listen( new Runnable() { public void run() { area.setText( area.getText() + "\n" + w.text); }});
	}
	
}

