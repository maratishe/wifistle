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

public class WiFi {
	public String mode = "scan"; // scan / hotspot
	public String filepath = "/sdcard/trace." + System.currentTimeMillis() + ".txt";
	// sensor
	private WifiManager wifi;
	private WifiConfiguration conf;
	File file;
	FileOutputStream trace; 
	Activity me;
	public void init( Activity me2) {
		me = me2;
		wifi = ( WifiManager)me.getSystemService( Context.WIFI_SERVICE); 
 		conf = new WifiConfiguration(); 
 		if ( ! wifi.isWifiEnabled()) wifi.setWifiEnabled( true);
 		wifiapoff(); mode = "scan";
	}
	// passive = scan WiFi APs
	public void listen( String filepath2) { try { 
		filepath = filepath2; file = new File( filepath); trace = new FileOutputStream( file, true); 
		final IntentFilter filter = new IntentFilter();
		filter.addAction( WifiManager.RSSI_CHANGED_ACTION); // you can keep this filter if you want to get fresh results when singnal stregth of the APs was changed
		filter.addAction( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		final BroadcastReceiver receiver = new BroadcastReceiver() { public void onReceive( Context context, Intent intent) { scan(); }}; 
		me.registerReceiver( receiver, filter); 
	} catch ( Exception e) { }}
	public void scan() { try { 
		List<ScanResult> results = wifi.getScanResults(); if ( results == null) return; int count = 0; 
		trace.write( ( new String( "time=" + System.currentTimeMillis())).getBytes());
		//OutputStreamWriter outw = new OutputStreamWriter( trace); 
		for ( ScanResult result : results) { // SSID, level 
			//if ( aps.containsKey( result.SSID)) continue; // already part of the hash
			//if ( ! result.SSID.substring( 0, 3).equals( "wea")) continue; // not a WEA network, ignore
			trace.write( ( new String( result.SSID + "=" + result.level + ",")).getBytes());
			count++;
		}
		trace.write( ( new String( System.getProperty("line.separator"))).getBytes());
	} catch ( Exception e) { }}
	public void stop() { try { trace.flush(); trace.close(); } catch ( Exception e) { }}
	// active = hotspot mode
	public void hotspot( String ssid) { 
		wifi.setWifiEnabled( false); 
		conf.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.OPEN);
		conf.allowedProtocols.set( WifiConfiguration.Protocol.RSN);
		conf.allowedProtocols.set( WifiConfiguration.Protocol.WPA);
		conf.allowedKeyManagement.set( WifiConfiguration.KeyMgmt.WPA_PSK);
		conf.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.TKIP);
		conf.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.CCMP);
		conf.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.TKIP);
		conf.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.CCMP);
		conf.SSID = ssid;
		conf.preSharedKey = "9080706051";
		wifiapon(); // toggle of the SEND button and 
	}
	public void wifiapoff() { try { 
		Method setWifiApMethod = wifi.getClass().getMethod( "setWifiApEnabled", WifiConfiguration.class, boolean.class);
		boolean apstatus = ( Boolean)setWifiApMethod.invoke( wifi, conf, false);
	} catch( Exception e) { }}
	public void wifiapon() { try { // invoking WiFi AP
		Method method = wifi.getClass().getMethod( "setWifiApEnabled", WifiConfiguration.class, boolean.class);
		method.invoke( wifi, conf, true);
		
		//Method isWifiApEnabledmethod = wifi.getClass().getMethod( "isWifiApEnabled");
		//if ( ! (Boolean)isWifiApEnabledmethod.invoke( wifi)) {};
		
		//Method getWifiApStateMethod = wifi.getClass().getMethod( "getWifiApState");
		//int apstate = ( Integer)getWifiApStateMethod.invoke( wifi);
		
		//Method getWifiApConfigurationMethod = wifi.getClass().getMethod( "getWifiApConfiguration");
		//conf = ( WifiConfiguration)getWifiApConfigurationMethod.invoke( wifi);
		//Log.i("Writing HotspotData", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");
		
	} catch( Exception e) {  }}
	public boolean iswifiapon() { try {
		Method method = wifi.getClass().getDeclaredMethod( "isWifiApEnabled");
		method.setAccessible( true);
		return ( Boolean)method.invoke( wifi);
	} catch (Throwable ignored) { }; return false; }
	
}

