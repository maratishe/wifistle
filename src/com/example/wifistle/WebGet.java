package com.example.wifistle;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.widget.EditText;
import android.content.res.Resources;
import android.util.TypedValue;

import android.net.wifi.WifiManager;
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


public class WebGet  { 
	public JSONObject json;
	public String BURL = "http://10.97.17.233:8001/api.php"; // configure once for all further calls
	private HttpURLConnection conn;
	public JSONObject call( String tail) { try { // returns JSONObject
		URL url = new URL( BURL + "?" + tail); 
		conn = ( HttpURLConnection)url.openConnection(); 
		BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream()));
		StringBuilder msg = new StringBuilder();
		String line; while ( ( line = reader.readLine()) != null) msg.append( line).append( "\n");
		reader.close(); //text.setText( stringBuilder.toString());
		json = ( JSONObject) new JSONTokener( msg.toString()).nextValue();
		//text.setText( "String > JSON > String: " + json.toString() + "\n\n");
		//text.setText( text.getText() + "1st msg: " + json.getJSONArray( "msgs").getString( 0));
	} catch( Exception e) { conn.disconnect(); return null; }; conn.disconnect(); return json; }
	public boolean has( String key) { return json.has( key); }
	public String get( String key) { try { String v = json.getString( key); return v; } catch ( Exception e) { return null; }}
}
