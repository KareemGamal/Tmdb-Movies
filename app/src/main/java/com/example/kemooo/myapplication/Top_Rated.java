package com.example.kemooo.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Top_Rated extends AppCompatActivity implements SearchView.OnQueryTextListener {


    String imgUrl = "http://image.tmdb.org/t/p/w500";
    Myadapter myadapter2;
    GridView gv;

    String Rotten_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        // Do it on Application start
        Toast.makeText(getApplicationContext(), "Here Is The List of Top Rated Films", Toast.LENGTH_SHORT).show();

///////////////////// initialize spinner with desired No.pages
        Integer[] page_num = new Integer[267];
        for (int i = 1; i <= page_num.length; i++) {
            page_num[i - 1] = i;
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, page_num);
        final Spinner sp = (Spinner) findViewById(R.id.spinner);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = sp.getSelectedItemPosition() + 1;
                new Tmdb().execute("http://api.themoviedb.org/3/movie/top_rated?api_key=36238a089d7b9497ccba2af9e2b8cc06&page=" + pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
///////////////////////////////////////////////////////////////////


    }


    ////////////////////// Search Box ////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!(validate_searchbox(query))) {
            Toast.makeText(this, "please Enter only alphabets with/without spaces !!", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(Top_Rated.this, Searching.class);
            i.putExtra("Movie", query);
            startActivity(i);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
////////////////////////////////////////////////////////////////////////////


    public class Tmdb extends AsyncTask<String, String, List<MoviesList>> {

        URL url;
        HttpURLConnection connection;
        BufferedReader reader;


        @Override
        protected List<MoviesList> doInBackground(String... params) {

            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line);
                }

                String Json = buffer.toString();
                JSONObject parent = new JSONObject(Json);
                JSONArray Movies = parent.getJSONArray("results");

                List<MoviesList> list = new ArrayList<>();
                for (int i = 0; i < Movies.length(); i++) {
                    JSONObject indexes = Movies.getJSONObject(i);
                    MoviesList mi = new MoviesList();
                    mi.setName(indexes.getString("title"));
                    mi.setImage(imgUrl + indexes.getString("poster_path"));
                    mi.setId(indexes.getString("id"));

                    list.add(mi);
                }

                return list;

            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MoviesList> moviesLists) {
            super.onPostExecute(moviesLists);
            myadapter2 = new Myadapter(Top_Rated.this, R.layout.movie, moviesLists);
            gv = (GridView) findViewById(R.id.gridview);
            gv.setAdapter(myadapter2);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(Top_Rated.this, "Loading please wait...", Toast.LENGTH_SHORT).show();


                    TextView name = (TextView) view.findViewById(R.id.m);
                    TextView iden = (TextView) view.findViewById(R.id.id);
                    String rot_name = validateMoviename(name.getText().toString());
                    new Rotten().execute("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=7ue5rxaj9xn4mhbmsuexug54&q=" +rot_name+"&page_limit=1");
                    Toast.makeText(Top_Rated.this, ""+ Rotten_id, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Top_Rated.this,  Rotten_id+ " " +rot_name, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Top_Rated.this, movie_detail.class);
                    i.putExtra("Name", name.getText().toString());
                    i.putExtra("id", iden.getText().toString());
                    i.putExtra("Rottenid", Rotten_id);
                    startActivity(i);
                }
            });

        }
    }


    public class Rotten extends AsyncTask<String, String, String> {

        URL url;
        HttpURLConnection connection;
        BufferedReader reader;


        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line);
                }

                String Json = buffer.toString();
                JSONObject parent = new JSONObject(Json);
                JSONArray results = parent.getJSONArray("movies");
                JSONObject index = results.getJSONObject(0);

                Rotten_id = index.getString("id");


                return  Rotten_id;


            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Rotten_id=s;
        }

    }


    public class Myadapter extends ArrayAdapter {
        public List<MoviesList> movielist;
        int resource;
        LayoutInflater inflater;

        public Myadapter(Context context, int resource, List<MoviesList> objects) {
            super(context, resource, objects);
            this.resource = resource;
            this.movielist = objects;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View v2 = inflater.inflate(R.layout.movie, null);


            TextView name = (TextView) v2.findViewById(R.id.m);
            TextView id = (TextView) v2.findViewById(R.id.id);

            id.setText(movielist.get(position).getId());
            ImageView image = (ImageView) v2.findViewById(R.id.main_img);


            name.setText(movielist.get(position).getName());
            id.setText(movielist.get(position).getId());


            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(movielist.get(position).getImage(), image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            return v2;
        }
    }


    public boolean validate_searchbox(String query) {
        String search = "^[\\p{L}\\d ]+(?:\\s[\\p{L}\\d ]+)*$";
        Pattern pattern = Pattern.compile(search);
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }


    public String validateMoviename(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == ' ') {
                c = '+';
                name = name.substring(0, i) + c + name.substring(i + 1, name.length()); //URL must be as http://www.omdbapi.com/?t=now+you+see+me+&y=&plot=short&r=json (" " not used, we use +)
            }

        }

        return name;
    }



}
