package com.example.wifistle;
                   
import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

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
import java.util.StringTokenizer;
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

import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

import android.os.CountDownTimer;

public class Whistle {
	// this is outside setup
	public int duration = 5000; // ms
	public String freqlist; // list of dot-delimited frequencies in Hz
	public double loudness = 1.0; // loudness
	public double level = 1.0;
	// this is mostly a fixed setup
	public int[] freqs; // hz, multiple options
	public int srate = 44100; // sample rate
	public int count = 10; // count
	public double sample[];
	public byte buffer[];
	public Runnable donec;
	public String msg = "init";
	public CountDownTimer timer;
	public AudioTrack a;
	public int pos = 0;
	public boolean reported = false; 
	//Handler handler = new Handler();
	public void prepare() { // prepares AudioTrack and generates the (multi-freq) tone
		count = ( duration / 1000) * srate; 
		sample = new double[ count];
		buffer = new byte[ 2 * count];
		a = new AudioTrack( AudioManager.STREAM_MUSIC,
			srate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT, count * 2,
			AudioTrack.MODE_STATIC);
		a.setStereoVolume( a.getMaxVolume(), a.getMaxVolume());
		
		// split the freqlist string into individual frequencies
		StringTokenizer parser = new StringTokenizer( freqlist, ".");
		int templist[] = new int[ 10]; int freqcount = 0; // up to 10 temp freqs
		while ( freqcount < 10 && parser.hasMoreTokens()) { templist[ freqcount] = Integer.parseInt( ( String)parser.nextElement()); freqcount++; }
		freqs = new int[ freqcount]; for ( int i = 0; i < freqcount; i++) freqs[ i] = templist[ i]; 
		
		
		// fill out the array
		level = 1.0 / ( double)freqcount - 0.02; 
		for ( int ii = 0; ii < freqcount; ii++) for ( int i = 0; i < count; ++i) { if ( ii == 0) sample[ i] = 0; sample[ i] += level * loudness * Math.sin( 2 * Math.PI * i / ( srate / ( double)freqs[ ii])); }
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int pos = 0;
		for ( final double d : sample) {
			// scale to maximum amplitude
			final short v = (short) (( d * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			buffer[ pos++] = (byte) ( v & 0x00ff);
			buffer[ pos++] = (byte) ( ( v & 0xff00) >>> 8);
		}
		
	}
	public void run( Runnable donec2) {  donec = donec2; final Thread thread = new Thread( new Runnable() { public void run() { play(); }}); thread.start(); }
	public void play() {
		a.write( buffer, 0, buffer.length); 
		//a.setNotificationMarkerPosition( buffer.length - 1000);
		a.setPositionNotificationPeriod( 5000);
		a.setPlaybackPositionUpdateListener( new AudioTrack.OnPlaybackPositionUpdateListener() { public void onMarkerReached( AudioTrack a2) { }; public void onPeriodicNotification( AudioTrack a2) {
			if ( reported) return; pos = a2.getPlaybackHeadPosition(); 
			//donec.run();
			if ( pos >= count - count / 10) { reported = true; donec.run(); } // notify completion at about 10% left  
		}});
		a.play();
	}
	
}


