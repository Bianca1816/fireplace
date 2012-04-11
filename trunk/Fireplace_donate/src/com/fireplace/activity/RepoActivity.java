package com.fireplace.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fireplace.software.R;

public class RepoActivity extends ListActivity implements OnClickListener{
    /** Called when the activity is first created. */
    
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ListView lv;

    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MUCH TIMES BUTTON WAS CLICKED
    int clickCounter=1;
    int itemPosition;
    String repoSmashup = "";
    ArrayList<String> repoArrayList = new ArrayList<String>();
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo);
        setTitle("Repositories");
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String currentItem = adapter.getItem(position);
				Intent i = new Intent(getApplicationContext(),
						GetContentFromDBActivity.class);
				i.putExtra("repo", currentItem);
				i.putExtra("position", 0);
				startActivity(i);
			}
		});
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemPosition = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(RepoActivity.this);

				String msg = "Remove repository from list?";

				builder.setMessage(msg)
						.setCancelable(true)
						.setTitle("Delete")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										listItems.remove(itemPosition);
							    		adapter.notifyDataSetChanged();
							    		dialog.cancel();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			}
		});
            
        Button btnAddRepo = (Button) findViewById(R.id.addBtn);
        btnAddRepo.setOnClickListener(this);
//        listItems.add("http://fireplace-market.com/getdata.php");
    }
    
    public void onClick(View v) {
    	
    	switch (v.getId()){
    	
    	case R.id.addBtn:
    		EditText txtAddRepo= (EditText) findViewById(R.id.txtAddRepo);
    		EditText text = (EditText) findViewById(R.id.txtAddRepo);
    		String textstring = text.getText().toString();
    		listItems.add(textstring);
    		adapter.notifyDataSetChanged();
    		txtAddRepo.setText("");
    		break;
    
    	}
    }
    
    private void getPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		repoSmashup = prefs.getString("repos", "");
		if(repoSmashup.length() > 0){
			List<String> wordList = Arrays.asList(repoSmashup.split("~")); 
			repoArrayList.addAll(wordList);
			for(String repos: repoArrayList){
				listItems.add(repos);
			}
			adapter.notifyDataSetChanged();
		}
	}
    
    private void setPrefs() {
    	SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
    	SharedPreferences.Editor editor = prefs.edit();
    	String repoString = "";
    	if (listItems.size() > 0){
    		for(String item : listItems){
    			repoString+= item + "~";
    		}
    	}
    	editor.putString("repos", repoString);
    	editor.commit();
	}

	@Override
	protected void onDestroy() {
		setPrefs();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		getPrefs();
		super.onResume();
	}
}