package org.me.gcu.trafficscotlandapp;

/**
 * Christopher Conlan
 * Created On: 14/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.FeedModelViewHolder> implements Filterable {

    private List<ItemModel> listItem;
    private List<ItemModel> listItemFull;


    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {

        private View FeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            FeedView = v;
        }
    }



    //Bind list to ItemRecyclerAdapter
    public ItemRecyclerAdapter(List<ItemModel> listItem) {

        this.listItem = listItem;
        listItemFull = new ArrayList<>(listItem);
    }

    //On create get RssFeed
    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_feed_viewer, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final ItemModel itemModel = listItem.get(position);
        ((TextView)holder.FeedView.findViewById(R.id.titleText)).setText(itemModel.title);
        ((TextView)holder.FeedView.findViewById(R.id.descriptionText)).setText(itemModel.description);
        ((TextView)holder.FeedView.findViewById(R.id.linkText)).setText(itemModel.link);


    }

    //Net number of items in the list.
    @Override
    public int getItemCount() {

        return listItem.size();
    }

    //Create Filter, this getFilter method will filter the list when searching for a specific item.
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ItemModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(listItemFull);
            } else {
                //This will create a new string that is not case sensitive and removes
                //empty spaces at beginning and end of input.
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ItemModel item : listItemFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

            }

            //Filter list and return a result.
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        //Data list will now will only contain filtered items. Adapter is told to refresh List.
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            listItem.clear();
            listItem.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };

}


