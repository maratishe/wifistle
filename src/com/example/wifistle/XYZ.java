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

public class XYZ implements SensorEventListener {
	// setup parameters for outside
	public String filepath = "/sdcard/trace." + System.currentTimeMillis() + ".txt";
	// remaining  configuration is (mostly) fixed
	public String mode;                  
	File file;
	FileOutputStream trace;
	private SensorManager smng;
	private Sensor sacc;
	private long lastime = 0; 
	private long time;
	private float lastx, lasty, lastz;
	private int diffx, diffy, diffz, diffms;
	Activity me;
	// passive = reading mode
	public void init( Activity me2) {
		me = me2;
		smng = ( SensorManager)me.getSystemService( Context.SENSOR_SERVICE);
    		sacc = smng.getDefaultSensor( Sensor.TYPE_ACCELEROMETER);
 		//stop(); 
	}
	public void start() { try { file = new File( filepath); trace = new FileOutputStream( file, true); smng.registerListener( this, sacc, SensorManager.SENSOR_DELAY_NORMAL); } catch ( Exception e) {}} // SENSOR_DELAY_FASTEST
	public void stop() { try { smng.unregisterListener( this); trace.close(); } catch ( Exception e) { }}
	public void onSensorChanged( SensorEvent e) { if ( e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) try {
		float x = e.values[ 0]; float y = e.values[ 1]; float z = e.values[ 2]; time = System.currentTimeMillis();
		if ( lastime == 0) { lastx = x; lasty = y; lastz = z; lastime = time; } // initialization
		if ( time - lastime < 100) return; // avoid too frequent polling
		diffx = ( int)( 100000 * ( x - lastx)); diffy = ( int)( 100000 * ( y - lasty)); diffz = ( int)( 100000 * ( z - lastz)); diffms = ( int)( 1000 * ( time - lastime)); 
		lastime = time; lastx = x; lasty = y; lastz = z;
		trace.write( ( new String( "time=" + time + ",diffx=" + diffx + ",diffy=" + diffy + ",diffz=" + diffz + ",diffms=" + diffms + ",")).getBytes());
		trace.write( ( new String( System.getProperty("line.separator"))).getBytes()); 
	} catch ( Exception e2) { }}
	public void onAccuracyChanged( Sensor sensor, int accuracy) {  }
}      

