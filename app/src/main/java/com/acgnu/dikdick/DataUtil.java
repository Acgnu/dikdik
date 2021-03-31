package com.acgnu.dikdick;

import android.content.Context;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class DataUtil {
  private JSONObject mJosnData;
  public static final String DATA_FILE = "data";
  private static DataUtil dataUtil = new DataUtil();

  private DataUtil(){}

  public static DataUtil instance(){
    return dataUtil;
  }

  public JSONObject readJson(Context context){
    try {
      File file = new File(context.getFilesDir(), DATA_FILE);
      if(!file.exists()){
          file.createNewFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    try (FileInputStream fis = context.openFileInput(DATA_FILE);
        InputStreamReader inputStreamReader =
            new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader)) {
      StringBuilder stringBuilder = new StringBuilder();
      String line = reader.readLine();
      while (line != null) {
        stringBuilder.append(line);
        line = reader.readLine();
      }
      mJosnData = stringBuilder.length() > 0 ? new JSONObject(stringBuilder.toString()) : new JSONObject();
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
      mJosnData = new JSONObject();
    }
    return mJosnData;
  }

  public void addNew(Context context, String title, int speed){
    try (FileOutputStream fos = context
        .openFileOutput(DATA_FILE, Context.MODE_PRIVATE)) {
      mJosnData.put(title, speed);
      fos.write(mJosnData.toString().getBytes());
    } catch (Exception e){
      e.printStackTrace();
      Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  public void del(Context context, String title){
    try (FileOutputStream fos = context
        .openFileOutput(DATA_FILE, Context.MODE_PRIVATE)) {
      mJosnData.remove(title);
      fos.write(mJosnData.toString().getBytes());
    } catch (Exception e){
      e.printStackTrace();
      Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
}
