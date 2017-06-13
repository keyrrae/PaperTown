package edu.ucsb.cs.cs190i.papertown;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ucsb.cs.cs190i.papertown.models.Town;

public class ListTownAdapter extends RecyclerView.Adapter<ListTownAdapter.ListTownViewHolder>{

    public List<Town> towns;


    public class ListTownViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView categoryTextView;
        public TextView descriptionTextView;
        public TextView likeCountTextView;
        public TextView list_town_author;
        public TextView date;

        public ListTownViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.list_town_image);
            titleTextView = (TextView) itemView.findViewById(R.id.list_town_title);
            categoryTextView = (TextView) itemView.findViewById(R.id.list_town_category);
            descriptionTextView = (TextView) itemView.findViewById(R.id.list_town_description);
            likeCountTextView =  (TextView) itemView.findViewById(R.id.list_favor_count);
            list_town_author =  (TextView) itemView.findViewById(R.id.list_town_author);
            date =  (TextView) itemView.findViewById(R.id.list_town_date);

        }
    }

    public ListTownAdapter(List<Town> towns){
        this.towns = towns;
    }

    @Override
    public ListTownAdapter.ListTownViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_town,parent,false);
        ListTownViewHolder holder = new ListTownViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListTownAdapter.ListTownViewHolder holder, int position) {
        Picasso.with(holder.imageView.getContext()).load(Uri.parse(towns.get(position).getImageUrls().get(0)))
                .error(R.drawable.dummyimage)
                .placeholder(R.drawable.dummyimage)
                .into(holder.imageView);
        holder.titleTextView.setText(towns.get(position).getTitle());
        holder.categoryTextView.setText(towns.get(position).getCategory());
        if(towns.get(position).getDescription()!=null&&towns.get(position).getDescription().size()>0) {
            holder.descriptionTextView.setText(towns.get(position).getDescription().get(0));
        }
        holder.likeCountTextView.setText(""+towns.get(position).getNumOfLikes());
        holder.list_town_author.setText(""+towns.get(position).getAuthor());

        if(towns.get(position).getDate()!=null&&!towns.get(position).getDate().isEmpty()){
            holder.date.setText(""+towns.get(position).getDate());
        }
        else{
            holder.date.setText("Unknown upload date.");
        }



    }

    @Override
    public int getItemCount() {
        return towns.size();
    }


}
