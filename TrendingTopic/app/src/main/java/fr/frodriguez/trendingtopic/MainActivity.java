package fr.frodriguez.trendingtopic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    protected String name;
    protected String query;
    protected String url;

    protected int searchCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // service d'authentification pour utiliser l'api twitter
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("Zn4nPDBCy6AQctonRzVGlAsHj")
                        .setOAuthConsumerSecret("nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA")
                        .setOAuthAccessToken("210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu")
                        .setOAuthAccessTokenSecret("ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb");

                Twitter twitter = new TwitterFactory(cb.build()).getInstance();

                try {
                    // récupérer les TT, 1 = monde entier
                    Trends placeTrends = twitter.getPlaceTrends(1);
                    Trend[] trends = placeTrends.getTrends();

                    Log.d("DEBUG", "TT ok");

                    // Prendre uniquement le 1er
                    if(trends[0] != null) {
                        name  = trends[0].getName();
                        query = trends[0].getQuery();
                        url   = trends[0].getURL();
                        searchCode = 0;
                    }
                    else {
                        searchCode = 1;
                    }

                } catch (TwitterException e) {
                    searchCode = -1;
                    e.printStackTrace();
                }
            }
        });

        // lancer le thread
        t.start();

        try {
            // attendre la fin de l'exécution
            t.join();

            switch(searchCode) {
                case 0:{
                    Toast.makeText(
                            this,
                            "1st TT found :)",
                            Toast.LENGTH_SHORT
                    ).show();

                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.query)).setText(query);
                    ((TextView) findViewById(R.id.url)).setText(url);

                    break;
                }
                case 1: {
                    Toast.makeText(
                            this,
                            "No TT available :(",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                }
                case -1: {
                    Toast.makeText(
                            this,
                            "Twitter services or network are unavailable :(",
                            Toast.LENGTH_LONG
                    ).show();
                    break;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}