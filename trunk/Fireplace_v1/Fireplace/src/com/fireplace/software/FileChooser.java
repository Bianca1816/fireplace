package com.fireplace.software;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooser extends ListActivity {
    
    private File currentDir;
    private FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/sdcard/Fireplace/com.sources.packages");
        fill(currentDir);
    }
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
         this.setTitle("All apps");
         List<Option>dir = new ArrayList<Option>();
         List<Option>fls = new ArrayList<Option>();
         try{
             for(File ff: dirs)
             {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"",ff.getAbsolutePath()));
                	
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
             }
         }catch(Exception e)
         {
             
         }
         Collections.sort(dir);
         Collections.sort(fls);
         dir.addAll(fls);
         
         adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
         this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
                currentDir = new File(o.getPath());
                fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(Option o)
    {
        Toast.makeText(this, "/sdcard/Fireplace/com.sources.packages" + File.separator + o.getName(), Toast.LENGTH_SHORT).show();
        
        File appFile = new File("/sdcard/Fireplace/com.sources.packages" + File.separator + o.getName());
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(Uri.fromFile(appFile),"application/vnd.android.package-archive");
        startActivity(installIntent);

        
    }
    


}
