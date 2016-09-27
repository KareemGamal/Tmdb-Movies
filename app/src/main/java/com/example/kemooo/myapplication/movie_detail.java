package com.example.kemooo.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
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

public class movie_detail extends AppCompatActivity {
    String imgUrl="http://image.tmdb.org/t/p/w500";
    String img2,Moviename , year , budget , revenue , story , genre , runtime , posterr , youtube_Key;
    float rate;
    AQuery query , query2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        // Do it on Application start

        query = new AQuery(movie_detail.this);
        query2 = new AQuery(movie_detail.this);
        Intent i = getIntent();
        String name= i.getExtras().getString("Name");
        final String id = i.getExtras().getString("id");
        String modified_Name=validateMoviename(name);

        ImageButton trail= (ImageButton) findViewById(R.id.Trailer);
        trail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Trailer().execute("http://api.themoviedb.org/3/movie/"+id+"/videos?api_key=36238a089d7b9497ccba2af9e2b8cc06");
            }
        });



        new Tmdb().execute("http://api.themoviedb.org/3/movie/"+ id +"?api_key=36238a089d7b9497ccba2af9e2b8cc06");
    }


    public class Tmdb extends AsyncTask< String , String ,String  > {

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

                Moviename=parent.getString("title");
                story=parent.getString("overview");
                budget=parent.getString("budget");
                revenue=parent.getString("revenue");
                img2=parent.getString("backdrop_path");
                year=parent.getString("release_date");
                runtime=parent.getString("runtime");
                rate=(float)parent.getDouble("vote_average");
                posterr = parent.getString("poster_path");

                JSONArray genres= parent.getJSONArray("genres");
                for(int i = 0 ;  i < genres.length(); i++)
                {
                    JSONObject index=genres.getJSONObject(i);



                }



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

            TextView mn= (TextView)findViewById(R.id.mName);
            mn.setText(Moviename);

            TextView Year= (TextView)findViewById(R.id.mYear);
            Year.setText("Release Year: "+ year);

            TextView Runtime= (TextView)findViewById(R.id.mRuntime);
            Runtime.setText("Runtime: "+runtime + " Min");
//
            TextView Story= (TextView)findViewById(R.id.s);
            Story.setText("Story" + story);
//
            TextView Budget= (TextView)findViewById(R.id.c);
            Budget.setText(budget + " $");

            TextView genres = (TextView)findViewById(R.id.g);
            genres.setText(genre+"   ");
//
            TextView Revenue= (TextView)findViewById(R.id.r);
            Revenue.setText(revenue + " $");

//
            RatingBar rb= (RatingBar)findViewById(R.id.ratingBar3);
            rb.setRating(rate/2);
//
            ImageView dropback= (ImageView)findViewById(R.id.dropback);
            query.id(R.id.dropback).image(imgUrl+img2);

//
            ImageView poster = (ImageView)findViewById(R.id.poster);
//            query2.id(R.id.poster).image(imgUrl+poster);


            ImageLoader.getInstance().displayImage(imgUrl+posterr, poster, new ImageLoadingListener() {
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
        }


        }



    public class Trailer extends AsyncTask< String , String ,String  > {

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
                JSONArray results=parent.getJSONArray("results");

                  JSONObject indexes=results.getJSONObject(0);
                youtube_Key=indexes.getString("key");

                
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

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.youtube.com/watch?v="+youtube_Key));
            startActivity(intent);
        }

        }







    public String validateMoviename(String name){
        for(int i = 0 ; i < name.length(); i++)
        {
            char c = name.charAt(i);
            if(c == ' '){
                c = '+';
                name = name.substring(0 , i) + c + name.substring( i+1 , name.length()); //URL must be as http://www.omdbapi.com/?t=now+you+see+me+&y=&plot=short&r=json (" " not used, we use +)
            }

        }


        return name;
    }
    }

