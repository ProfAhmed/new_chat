package com.pro.ahmed.navigationdrawertest.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.LikesNamesAdapter;
import com.pro.ahmed.navigationdrawertest.model.Like;

import java.util.List;

/**
 * Created by hp on 11/10/2017.
 */

public class CustomDialogContextMenuLikesNames extends Dialog {
    Context mContext;
    List<Like> likes;
    RecyclerView rvContextMenuLikeNames;

    public CustomDialogContextMenuLikesNames(Context mContext, List<Like> likes) {
        super(mContext);
        this.mContext = mContext;
        this.likes = likes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_context_menu_likes_names);
        rvContextMenuLikeNames = (RecyclerView) findViewById(R.id.rvContextMenuLikesNames);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvContextMenuLikeNames.setHasFixedSize(true);
        rvContextMenuLikeNames.setLayoutManager(llm);
        LikesNamesAdapter adapter = new LikesNamesAdapter(getContext(), likes);
        rvContextMenuLikeNames.setAdapter(adapter);
    }
}
