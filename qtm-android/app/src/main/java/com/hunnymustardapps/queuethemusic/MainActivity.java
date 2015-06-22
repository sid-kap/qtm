package com.hunnymustardapps.queuethemusic;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.hunnymustardapps.queuethemusic.adapters.SearchResultListAdapter;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "85400ecd0f834631826d892d640cfc6b";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "testing://ericlee123.com";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 62;

    private Player mPlayer;
    private List<String> _searchResults;
    private SearchResultListAdapter _srla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AuthenticationRequest.Builder builder =
//                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
//        builder.setScopes(new String[]{"user-read-private", "streaming"});
//        AuthenticationRequest request = builder.build();
//
//        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        _searchResults = new ArrayList<String>();
        _searchResults.add("fuck me");
        // setup listview
        ListView results = (ListView) findViewById(R.id.search_results);
        _srla = new SearchResultListAdapter(this, _searchResults);

        results.setAdapter(_srla);
    }

    public void search(View view) {

        String keywords = ((EditText) findViewById(R.id.spotify_search)).getText().toString();
        new HttpSearch().execute(keywords.trim());

    }

    private class HttpSearch extends AsyncTask<String, Void, Void> {

        private List<String> results;

        private void httpSearch(String query) {

            StringBuilder temp = new StringBuilder("https://api.spotify.com/v1/search?q=");
            query = query.replace(" ", "+");
            temp.append(query);
            temp.append("&type=track&limit=7");

            URL url;
            HttpURLConnection huc = null;
            String json = null;
            try {
                url = new URL(temp.toString());
                huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                String blah;
                StringBuffer sb = new StringBuffer();
                while ((blah = br.readLine()) != null) {
                    sb.append(blah);
                }
                br.close();
                json = sb.toString();

                results = new ArrayList<>();
                JSONObject jo = new JSONObject(json);
                jo = jo.getJSONObject("tracks");
                JSONArray ja = jo.getJSONArray("items");
                for (int i = 0; i < ja.length(); i++) {
                    results.add((ja.getJSONObject(i).getString("name")));
                    System.out.println((ja.getJSONObject(i).getString("name")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    huc.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Void doInBackground(String... queries) {
            httpSearch(queries[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (results != null) {
                _searchResults.clear();
                for (int i = 0; i < results.size(); i++) {
                    _searchResults.add(results.get(i));
                }
                System.out.println("here");
                _srla.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
//                        mPlayer.addConnectionStateCallback(MainActivity.this);
//                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
//                        mPlayer.play("spotify:track:5mQjdE8ujLsXjBrlOkUYBg");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}