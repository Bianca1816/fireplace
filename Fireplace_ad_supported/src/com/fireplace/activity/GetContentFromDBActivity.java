package com.fireplace.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.fireplace.software.ItemSkel;
import com.fireplace.adsup.R;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class GetContentFromDBActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listofapps);
        
        LoadData();
    }
    
    
    void GenData(){
    	ArrayList<ItemSkel> list = new ArrayList<ItemSkel>();
    	for(int i = 0; i< 10; ++i) {
    		list.add(new ItemSkel("id " + i, "label " + i, "path " + i, "description " + i, "ptype " + i, "devl " + i));
    	}

    	Gson gson = new Gson();
    	String json = gson.toJson(list);
    	
    	Toast.makeText(GetContentFromDBActivity.this, json, Toast.LENGTH_LONG).show();
    }
    
    public void btnLoadData(View v) {
    	LoadData();
    }
    
    
    void LoadData(){

        //TextView v = (TextView)findViewById(R.id.txtStatusError); 
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://www.u2worlds.com/fp/getdata.php");
            HttpResponse response = httpClient.execute(httpGet, localContext);
            String result = "";
             
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                  response.getEntity().getContent()
                )
              );
             
            String line = null;
            while ((line = reader.readLine()) != null)
              result += line;

    		Type type = new TypeToken<ArrayList<ItemSkel>>(){}.getType();
            Gson g = new Gson();
            final ArrayList<ItemSkel> list = g.fromJson(result, type);
            ArrayList<String> stringArray = new ArrayList<String>();
            for (ItemSkel item : list) 
            	stringArray.add("" + item.getLabel());
            
            ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
            ListView lv = (ListView)findViewById(R.id.lwApps); 
            lv.setAdapter(modeAdapter);
            
            lv.setOnItemClickListener(new OnItemClickListener() {
              public void onItemClick(AdapterView<?> parent, View view,
                  int position, long id) {
                // When clicked, show a toast with the TextView text
            	  ItemSkel currentItem = list.get(position);
            	  Intent i = new Intent(getApplicationContext(), ViewInfoActivity.class);
            	  i.putExtra("title", currentItem.getLabel());
            	  i.putExtra("icon", currentItem.getIcon());
            	  i.putExtra("link", currentItem.getPath());
            	  i.putExtra("desc", currentItem.getDescription());
            	  i.putExtra("ptype", currentItem.getPtype());
            	  startActivity(i);
            	  }
            });
			
            //Toast.makeText(GetContentFromDBActivity.this, "No Error", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
        	//v.setText(ex.getMessage());
        	Toast.makeText(GetContentFromDBActivity.this, "Could not connect!", Toast.LENGTH_LONG).show();
        }
    }
    public void updateProgress(int currentSize, int totalSize){
    	//Toast.makeText(this, "Packages: " + Long.toString((currentSize/totalSize)*100)+"% Complete", Toast.LENGTH_SHORT).show();
    	}
}

