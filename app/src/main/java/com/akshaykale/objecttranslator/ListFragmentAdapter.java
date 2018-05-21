package com.akshaykale.objecttranslator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshaykale.objecttranslator.models.MWord;
import com.akshaykale.objecttranslator.utils.ui.TextViewFont;

import java.util.ArrayList;

class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.CheckInOptionsViewHolder> {

    private ArrayList<MWord> map;

    public ListFragmentAdapter(ArrayList<MWord> map) {
        if (map == null)
            this.map = new ArrayList<>();
        this.map = map;
    }

    @NonNull
    @Override
    public CheckInOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_saved_words, parent, false);
        return new CheckInOptionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInOptionsViewHolder holder, int position) {
        MWord model = map.get(position);
        holder.srcText.setText(model.wordFrom);
        holder.srcLang.setText("( " + model.langFrom + " )");
        holder.destText.setText(model.wordTo);
        holder.destLang.setText("( " + model.langTo + " )");
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class CheckInOptionsViewHolder extends RecyclerView.ViewHolder {

        public TextViewFont srcText, destText, srcLang, destLang, date;

        public CheckInOptionsViewHolder(View itemView) {
            super(itemView);
            srcLang = itemView.findViewById(R.id.tv_src_lang);
            srcText = itemView.findViewById(R.id.tv_src_word);
            destLang = itemView.findViewById(R.id.tv_dest_lang);
            destText = itemView.findViewById(R.id.tv_dest_word);

        }
    }
}
