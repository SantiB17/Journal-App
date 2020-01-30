package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.santi.journalapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {//creates the views and implements the recycling
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row, viewGroup,false);// actions, so the android system is able to show
        return new ViewHolder(view,context);//pass view into the ViewHolder constructor, it can get all its items i.e. TextViews, ImageViews, etc.                                                             // all the data as users scroll up and down
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder viewHolder, int position) {
        Journal journal = journalList.get(position);; //gets one journal from list
        String imageUrl = journal.getImageURL();

        viewHolder.title.setText(journal.getTitle());
        viewHolder.thoughts.setText(journal.getThought());
        viewHolder.name.setText(journal.getUsername());

        Picasso.get().load(imageUrl).placeholder(R.drawable.image_three).fit()
                .into(viewHolder.image); //Use Picasso library to download and show image

        //Handling the date of the posted journal
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded()
                .getSeconds()*1000); //casting a char to a string
        viewHolder.dateAdded.setText(timeAgo);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, thoughts, dateAdded, name;
        public ImageView image;
        public ImageButton shareButton;
       // String userId, username;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.journal_title_list); //itemView used because JRA.java is NOT an Activity, so itemView, which is an XML object,
            thoughts = itemView.findViewById(R.id.journal_thought_list); //is allowed to use findViewbyID
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_username);
            shareButton = itemView.findViewById(R.id.journal_row_share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
