package ir.adicom.app.servicestest;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    String iconString = "";
    HttpURLConnection connection = null;
    InputStream inputStream = null;
    TextView txt,txtDesc;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.textview1);
        txtDesc = (TextView) findViewById(R.id.textView);

        url = "http://api.openweathermap.org/data/2.5/find?q=Ardabil&appid=d13615c0f9fd6171d8344ed5492a815a";
        getInfo();

        final EditText edt = (EditText) findViewById(R.id.editText);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "http://api.openweathermap.org/data/2.5/find?q="+edt.getText()+"&appid=d13615c0f9fd6171d8344ed5492a815a";
                getInfo();

            }
        });
    }

    public void getInfo() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.connect();


            //Read the response
            StringBuilder stringBuffer = new StringBuilder();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
                Log.e(TAG, "ResponseCode: " + responseCode);
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
            String b = "";
            b += "City: " + list.getString("name");
            b += "\nTemp: " + (int) (list.getJSONObject("main").getDouble("temp") - 273.15);
            txt.setText(b);
            iconString = list.getJSONArray("weather").getJSONObject(0).getString("icon");
            txtDesc.setText(new StringBuilder(list.getJSONArray("weather").getJSONObject(0).getString("main")).append("\n"+list.getJSONArray("weather").getJSONObject(0).getString("description")).toString());

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
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageBitmap(getImage(iconString));

    }


    public static Bitmap getImage(String code) {

        try {

            URL url = new URL("http://openweathermap.org/img/w/" + code +".png");
            //URL url = new URL("http://openweathermap.org/img/w/10n.png");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
           connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

            Log.v("URL: ", String.valueOf(url));

            return myBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage().toString());
            return null;
        }

    }

}
