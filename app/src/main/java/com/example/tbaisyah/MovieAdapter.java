package com.example.tbaisyah;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Movie> movieList;

    public MovieAdapter(Context context, int layout, ArrayList<Movie> movieList) {
        this.context = context;
        this.layout = layout;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtTitle,txtYear,txtGenre,txtDesc;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtYear = (TextView) row.findViewById(R.id.txtYear);
            holder.txtGenre = (TextView) row.findViewById(R.id.txtGenre);
            holder.txtDesc = (TextView) row.findViewById(R.id.txtDesc);
            holder.imageView = (ImageView) row.findViewById(R.id.imgMovie);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Movie movie = movieList.get(position);

        holder.txtTitle.setText(movie.getTitle());
        holder.txtYear.setText(movie.getYear());
        holder.txtGenre.setText(movie.getGenre());
        holder.txtDesc.setText(movie.getDesc());

        byte[] movieImage = movie.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(movieImage, 0, movieImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
