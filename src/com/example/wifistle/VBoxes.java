package com.example.wifistle;

import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TableLayout;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import android.util.DisplayMetrics;

import java.util.Hashtable;
import java.util.Set;
import java.util.Enumeration;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.graphics.PorterDuff;

public class VBoxes  {
	private Activity A;
	private DisplayMetrics dm; 
	private TableLayout B;
	private TableRow thisrow; 
	public VBoxes( Activity a, int bgcolor) { 
		A = a;
		B = new TableLayout( A);
		B.setVerticalScrollBarEnabled( true);
		B.setBackgroundColor( bgcolor);
		dm = new DisplayMetrics();
		A.getWindow().getWindowManager().getDefaultDisplay().getMetrics( dm);
	}
	public TableLayout getbody() { return B; }
	public TableRow getcurrentrow() { return thisrow; }
	public void params( View v, double width, double height, boolean enabled) { 
		if ( width > 0) v.getLayoutParams().width = ( int)( width * dm.widthPixels); 
		if ( height > 0) v.getLayoutParams().height = ( int)( height * ( double)dm.heightPixels); 
		v.setEnabled( enabled);
	}
	// components
	public TextView text( int color, boolean newrow, boolean fullrow) { 
		TableRow r = newrow ? new TableRow( A) : thisrow; 
		TextView text = new TextView( A);
		text.setTextColor( color);
		//text.setLayoutParams( new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		//text.setText( "Some Text");
		//text.setPadding( 2, 2, 2, 2);
		r.addView( text); if ( newrow) B.addView( r); thisrow = r; 
		if ( fullrow) { TableRow.LayoutParams params = (TableRow.LayoutParams)text.getLayoutParams(); params.span = 2; text.setLayoutParams( params); }
		return text;
	}
	public ToggleButton toggle( String on, String off, boolean state, boolean newrow, boolean fullrow) {
		TableRow r = newrow ? new TableRow( A) : thisrow;  
		ToggleButton t = new ToggleButton( A);
		t.setTextOn( on); t.setTextOff( off); t.setChecked( state);
		//t.setPadding( 2, 2, 2, 2);
		r.addView( t); if ( newrow) B.addView( r); thisrow = r; 
		if ( fullrow) { TableRow.LayoutParams params = (TableRow.LayoutParams)t.getLayoutParams(); params.span = 2; t.setLayoutParams( params); }
		return t;
	}
	public Button button( int color, String text, boolean newrow, boolean fullrow) {
		TableRow r = newrow ? new TableRow( A) : thisrow;
		Button b2 = new Button( A);
		b2.setText( text); 
		b2.setTextColor( color);
		r.addView( b2); if ( newrow) B.addView( r); thisrow = r; 
		if ( fullrow) { TableRow.LayoutParams params = (TableRow.LayoutParams)b2.getLayoutParams(); params.span = 2; b2.setLayoutParams( params); }
		return b2;
	}
	public EditText input( int bgcolor, int color, String text, boolean newrow, boolean fullrow) {
		TableRow r = newrow ? new TableRow( A) : thisrow;
		EditText e = new EditText( A);
		e.getBackground().setColorFilter( bgcolor, PorterDuff.Mode.SRC_ATOP);
		e.setText( text); 
		e.setTextColor( color);
		r.addView( e); if ( newrow) B.addView( r); thisrow = r; 
		if ( fullrow) { TableRow.LayoutParams params = (TableRow.LayoutParams)e.getLayoutParams(); params.span = 2; e.setLayoutParams( params); }
		return e;
	}
	// others
	public void clear() { B.removeAllViews(); }
}

