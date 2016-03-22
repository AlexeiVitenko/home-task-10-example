package com.example.avitenko.urlloader;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView mMainTextView;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NetworkTask().execute("http://api-fotki.yandex.ru/api/podhistory/poddate;2012-04-01T12:00:00Z/");
            }
        });
        mMainTextView = (TextView)findViewById(R.id.mainText);
        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(stream, null);
                    String imgUrl = null;
                    List<String> imgUrls = new ArrayList<>();
                    while ((imgUrl = processEntry(parser)) != null) {
                        imgUrls.add(imgUrl);
                    }
                    return imgUrls;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String prefix = parser.getPrefix();
                // Starts by looking for the entry tag
                if ("f".equals(prefix) && "img".equals(name)) {
                    for (int i = 0; i<parser.getAttributeCount(); i++) {
                        if ("height".equals(parser.getAttributeName(i))) {
                            if ("75".equals(parser.getAttributeValue(i))) {
                                return parser.getAttributeValue(i+1);
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> xml) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
            mRecyclerView.setAdapter(new ImagesAdapter(xml));
        }
    }
}
