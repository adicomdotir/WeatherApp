package ir.adicom.app.servicestest;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adicom on 9/28/16.
 */
public class AppWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()==null) {
            context.startService(new Intent(context, ToggleService.class));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ToggleService.class));
    }

    public static class ToggleService extends IntentService {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        public ToggleService() {
            super(ToggleService.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ComponentName me = new ComponentName(this, AppWidget.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);
            mgr.updateAppWidget(me, buildUpdate(this));
        }

        private RemoteViews buildUpdate(Context context) {
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            updateViews.setTextViewText(R.id.textView77, getInfo());
//            Intent i = new Intent(this, AppWidget.class);
//            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//            updateViews.setOnClickPendingIntent(R.id.textView77, pi);
            return updateViews;
        }

        public String getInfo() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String url = "http://api.openweathermap.org/data/2.5/find?q=Ardabil&appid=d13615c0f9fd6171d8344ed5492a815a";
            String b = "";
            try {
                connection = (HttpURLConnection) (new URL(url)).openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                //connection.setDoOutput(true);
                connection.connect();


                //Read the response
                StringBuilder stringBuffer = new StringBuilder();
                inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;

                String str = "";

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\r\n");
                    str += line;
                }

                String in = str;
                inputStream.close();
                connection.disconnect();

                JSONObject reader = new JSONObject(in);
                JSONArray arr = reader.getJSONArray("list");
                JSONObject list  = arr.getJSONObject(0);
                b = "";
                b += "City: " + list.getString("name");
                b += "\nTemp: " + (int) (list.getJSONObject("main").getDouble("temp") - 273.15);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(connection != null)
                    connection.disconnect();
            }
            return b;
        }
    }
}
