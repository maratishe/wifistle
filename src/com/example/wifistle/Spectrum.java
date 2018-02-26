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

//import ca.uol.aig.fftpack.RealDoubleFFT;

public class Spectrum {
	// open config (accessible from the outside
	public int low; // low and high cutoff freqs
	public int high; 
	public String filepath = "";
	public int duration = 5000;
	public boolean dumpEachPage = false;
	// mostly fixed setup below
	//public int blocksize = 2048; // 256 for 8000
	public int lowpos; 
	public int highpos; 
	public int srate = 44100; // sample rate 8000, 16000, 44100
	public int bsize; // buffersize
	public double power[];
	// (mostly) fixed config
	private static final int channels = AudioFormat.CHANNEL_IN_MONO;
	private static final int code = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord rec = null;
	private Thread thread = null;
	private boolean isactive = false;
	public File file;
	public FileOutputStream trace = null;
	//public RealDoubleFFT fft = null;
	public FFT fft = null; 
	private long lastime = 0; 
	private long firstime = 0; 
	private void sample() { try { short b[] = new short[ bsize]; double x[] = new double[ bsize]; double y[] = new double[ bsize]; double z[] = new double[ bsize]; while ( isactive) { 
		if ( System.currentTimeMillis() - firstime >= duration) { isactive = false; return; } // sample only within the duration interval
		
		for ( int i = 0; i < bsize; i++) b[ i] = 0;
		int bytes = rec.read( b, 0, bsize);
		
		// fftpack class
		//fft = new RealDoubleFFT( bsize);
		//for ( int i = 0; i < bsize && i < bytes; i++) { x[ i] = ( double)b[ i] / 32768.0; y[ i] = 0; } // signed 16 bit
		//fft.ft( x); 
		//for ( int i = 0; i < bsize / 2; i++) z[ i] = Math.sqrt( x[ i * 2] * x[ i * 2] + x[ i * 2 + 1] * x[ i * 2 + 1]);
		
		
		// columbia class (simpler, in this directory)
		fft = new FFT( bsize); 
		for ( int i = 0; i < bsize; i++) { x[ i] = ( double)b[ i]; y[ i] = 0; } 
		fft.fft( x, y);
		for ( int i = 0; i < bsize / 2; i++) z[ i] = Math.sqrt( x[ i] * x[ i] + y[ i] * y[ i]);
		
		if ( ! filepath.equals( "") && dumpEachPage) trace.write( ( new String( "" + ( System.currentTimeMillis() - lastime) + "ms")).getBytes());
		if ( ! filepath.equals( "") && dumpEachPage) for ( int i = 0; i < bsize / 2; i++) trace.write( ( new String( " " + z[ i])).getBytes());
		if ( ! filepath.equals( "") && dumpEachPage) trace.write( ( new String( System.getProperty( "line.separator"))).getBytes());
		
		// normalize
		//for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) if ( z[ i] == Double.NaN) z[ i] = 0; 
		//double min = z[ lowpos]; double max = z[ lowpos];
		//for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) if ( z[ i] < min) min = z[ i]; 
		//for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) if ( z[ i] > max) max = z[ i];
		//for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) z[ i] = ( double)Math.round( 10000.0 * ( z[ i] - min) / ( max - min));
		for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) power[ i] += z[ i]; // only update power for the selected freq band
		
		lastime = System.currentTimeMillis(); 
	}} catch ( Exception e) { }}
	public void start() { try { 
		bsize = AudioRecord.getMinBufferSize( srate, channels, code); // bsize = 512; // 2048
		//fft = new FFT( bsize); 
		power = new double[ bsize]; for ( int i = 0; i < bsize / 2; i++) power[ i] = 0; // power: power aggregate per frequency 
		for ( lowpos = 0; low > lowpos * srate / bsize; lowpos++) {} // defining positions where to cut off based on frequency
		for ( highpos = bsize / 2 - 1; high < highpos * srate / bsize; highpos--) {}
		
		if ( ! filepath.equals( "")) file = new File( filepath);
		if ( ! filepath.equals( "")) trace = new FileOutputStream( file, true); 
		if ( ! filepath.equals( "")) trace.write( ( new String( "# 1st line:  freqs in Hz    2+ lines: TimeSinceLast(ms) power power power (for current page)...       1beforelast line: final(normalized) power     last line: freq=power map" +  System.getProperty( "line.separator"))).getBytes()); 
		if ( ! filepath.equals( "")) for ( int i = 0; i < bsize / 2; i++) trace.write( ( new String( ( i * srate / bsize) + " ")).getBytes()); // 1st line : frequencies
		if ( ! filepath.equals( "")) trace.write( ( new String( System.getProperty( "line.separator"))).getBytes());
		
		rec = new AudioRecord( MediaRecorder.AudioSource.MIC, srate, channels, code, bsize);
		rec.startRecording();
		isactive = true; lastime = System.currentTimeMillis(); firstime = lastime; 
		thread = new Thread( new Runnable() { public void run() { sample(); } }, "AudioRecorder Thread");
		thread.start();
	} catch ( Exception e) { }}
	public void stop() { if ( null != rec) try { isactive = false; rec.stop(); rec.release(); rec = null; thread = null; } catch ( Exception e) { }}
	public void dump() { if ( filepath.equals( "")) return; try { // dump only if filename is specified
		double min = power[ lowpos]; double max = power[ lowpos]; 
		for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) if ( power[ i] < min) min = power[ i]; 
		for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) if ( power[ i] > max) max = power[ i];
		int output[] = new int[ bsize / 2]; for ( int i = 0; i < bsize / 2; i++) output[ i] = 0; 
		for ( int i = lowpos; i < bsize / 2 && i < highpos; i++) output[ i] = ( int)Math.round( 10000.0 * ( ( power[ i] - min) / ( max - min)));
		for ( int i = 0; i < bsize / 2; i++) trace.write( ( new String( output[ i] + " ")).getBytes()); // 1 before last line: normalized power
		trace.write( ( new String( System.getProperty( "line.separator"))).getBytes());
		for ( int i = 0; i < bsize / 2; i++) trace.write( ( new String( ( i * srate / bsize) + "=" + output[ i] + ",")).getBytes()); // last line:   freq=power,....  map
		trace.write( ( new String( System.getProperty( "line.separator"))).getBytes());
		trace.flush(); trace.close();
	} catch ( Exception e) { }}
	
}

