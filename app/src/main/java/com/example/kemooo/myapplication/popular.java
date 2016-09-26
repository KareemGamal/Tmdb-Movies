package com.example.kemooo.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class popular extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ProgressBar pb;
    String imgUrl="http://image.tmdb.org/t/p/w500";
    String ident;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        // Do it on Application start
        Toast.makeText(getApplicationContext(), "Here Is The List of popular Films", Toast.LENGTH_SHORT).show();

        new Tmdb().execute("http://api.themoviedb.org/3/movie/popular?api_key=36238a089d7b9497ccba2af9e2b8cc06");
    }

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
        Intent i = new Intent(popular.this, Searching.class);
        i.putExtra("Movie",query);
        startActivity(i);
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }



    public class Tmdb extends AsyncTask<String , String , List<MoviesList>  > {

        URL url;
        HttpURLConnection connection;
        BufferedReader reader;


        @Override
        protected List<MoviesList> doInBackground(String... params) {

            try {
                url = new URL(params[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream is=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer =new StringBuffer();
                String line="";
                while((line=reader.readLine()) != null){

                    buffer.append(line);
                }

                String Json= buffer.toString();
                JSONObject parent=new JSONObject(Json);
                JSONArray Movies=parent.getJSONArray("results");

                List<MoviesList> list= new ArrayList<>();
                for(int i = 0 ; i< Movies.length() ; i++)
                {
                    JSONObject indexes = Movies.getJSONObject(i);
                    MoviesList mi = new MoviesList();
                    mi.setName(indexes.getString("title"));
                    mi.setImage(imgUrl+indexes.getString("poster_path"));
                    mi.setId(indexes.getString("id"));

                    list.add(mi);
                }

                return  list;

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
            Myadapter myadapter2= new Myadapter(getApplicationContext(), R.layout.movie, moviesLists);
            GridView gv= (GridView) findViewById(R.id.gridview2);
            gv.setAdapter(myadapter2);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(popular.this, "Loading please wait...", Toast.LENGTH_SHORT).show();

                    TextView name= (TextView)view.findViewById(R.id.m);
                    TextView iden = (TextView)view.findViewById(R.id.id);
                    Intent i = new Intent(popular.this, movie_detail.class);
                    i.putExtra("Name",name.getText().toString());
                    i.putExtra("id",iden.getText().toString());
                    startActivity(i);
                }
            });

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


            TextView name= (TextView)v2.findViewById(R.id.m);
            TextView id= (TextView)v2. findViewById(R.id.id);

            id.setText(movielist.get(position).getId());

            ImageView image = (ImageView) v2.findViewById(R.id.main_img);

            name.setText(movielist.get(position).getName());
            ident= movielist.get(position).getId();



//            pb = (ProgressBar) v2.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(movielist.get(position).getImage(), image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
//                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
//                    pb.setVisibility(View.GONE);
                }
            });
            // Default options will be used

            return v2;
        }
    }
}
