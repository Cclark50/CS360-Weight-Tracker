package com.snhu.ProjectTwo.utilities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snhu.ProjectTwo.R;
import com.snhu.ProjectTwo.interfaces.OnItemActionListener;

import java.util.List;


//@Author Christian Clark
//@Date 8-14-25

//adapter for a recycler view that allows for a list of cards to exist in the recyclerview
//and are able to be scrolled
public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.ViewHolder> {

    private final OnItemActionListener _deleteClicked;


    private List<UserInfo> _items;
    private OnItemClickListener _listener;
    private Context _context;
    private long _userId;

    public DatabaseAdapter(Context context, List<UserInfo> info, long id, OnItemActionListener actionListener){
        _context = context;
        _items = info;
        _userId = id;
        _deleteClicked = actionListener;
    }

    public interface OnItemClickListener{
        void onItemClick(UserInfo info, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        _listener = listener;
    }

    //View holder for each card to be placed inside the recycler view list
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateText;
        public TextView weightText;
        public ImageButton deleteButton;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            dateText = itemView.findViewById(R.id.card_date_text);
            weightText = itemView.findViewById(R.id.card_weight_text);
            deleteButton = itemView.findViewById(R.id.card_delete_button);

        }

        public void bind(UserInfo item, OnItemClickListener listener){
            dateText.setText(item.getDate());
            weightText.setText(String.valueOf(item.getWeight()));

            if(listener != null) {
                itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        int position = getBindingAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(item, position);
                        }
                    }
                });
            }
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_data_card, parent, false);
        return new ViewHolder(view);
    }

    //binds the callbacks from the OnItemActionListener to the delete button and date texts
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfo item = _items.get(position);
        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int currPos = holder.getBindingAdapterPosition();
                if(currPos != RecyclerView.NO_POSITION && _deleteClicked != null){
                    _deleteClicked.onDeleteClicked(_items.get(currPos), currPos);
                }
            }
        });
        holder.dateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int currPos = holder.getBindingAdapterPosition();
                if(currPos != RecyclerView.NO_POSITION && _deleteClicked != null){
                    _deleteClicked.onDateClicked(_items.get(currPos), currPos);
                }
            }
        });
        holder.bind(item, _listener);
    }

    @Override
    public int getItemCount(){
        return _items.size();
    }

    //purges and replaces the list
    public void updateData(List<UserInfo> newInfo){
        _items.clear();
        _items.addAll(newInfo);
        notifyDataSetChanged();
    }

}
