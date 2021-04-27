package com.Spiros.Nodyssia.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.Spiros.Nodyssia.R;

import java.util.List;

/**
 * Created by manel on 9/5/2017.
 */

public class cardAdapter extends ArrayAdapter<cardObject>{

    Context context;

    public cardAdapter(Context context, int resourceId, List<cardObject> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        cardObject card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(card_item.getName() + ", " + card_item.getAge());

        if(!card_item.getProfileImageUrl().equals("default"))
            Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);



        return convertView;

    }
}
