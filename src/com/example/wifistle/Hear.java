package com.example.wifistle;
                   
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

public class Hear {
	// open config (accessible from the outside
	public int srate = 44100; // sample rate 8000, 16000, 44100
	public String filepath = "/sdcard/trace." + System.currentTimeMillis() + ".txt";
	// (mostly) fixed config
	private static final int channels = AudioFormat.CHANNEL_IN_MONO;
	private static final int code = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord rec = null;
	private Thread thread = null;
	private boolean isactive = false;
	int bsize; // buffersize
	File file;
	FileOutputStream trace;
	private long lastime = 0; 
	private void writeAudioDataToFile() { short buffer[] = new short[ bsize]; while ( isactive) {
		// gets the voice output from microphone to byte format
		int bytes = rec.read( buffer, 0, bsize);
		int timepassed = ( int)( System.currentTimeMillis() - lastime); lastime = System.currentTimeMillis();
		try { trace.write( ( new String( "" + timepassed + " " + bytes)).getBytes()); for ( int i = 0; i < buffer.length && i < bytes; i++) trace.write( ( new String( " " + buffer[ i])).getBytes()); trace.write( ( new String( System.getProperty( "line.separator"))).getBytes()); } catch ( Exception e) { }
	}; try { trace.flush(); trace.close(); } catch ( Exception e) { }}
	public void start() {
		bsize = AudioRecord.getMinBufferSize( srate, channels, code); 
		file = new File( filepath);
		lastime = System.currentTimeMillis();
		try { trace = new FileOutputStream( file, true); trace.write( ( new String( "# ms.from.last count.per.line value value value" +  System.getProperty( "line.separator"))).getBytes()); } catch ( Exception e) { }
		rec = new AudioRecord( MediaRecorder.AudioSource.MIC, srate, channels, code, bsize);
		rec.startRecording();
		isactive = true;
		thread = new Thread( new Runnable() { public void run() { writeAudioDataToFile(); } }, "AudioRecorder Thread");
		thread.start();
	}
	public void stop() { if ( null != rec) try { isactive = false; trace.close(); rec.stop(); rec.release(); rec = null; thread = null; } catch ( Exception e) { }}
}

