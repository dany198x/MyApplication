package com.example.dany198x.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dany198x.myapplication.Entity.Comment;
import com.example.dany198x.myapplication.FoodDetailsActivity;
import com.example.dany198x.myapplication.R;
import com.example.dany198x.myapplication.constant.Constant;
import com.example.dany198x.myapplication.myView.SimpleRatingView;
import com.example.xlhratingbar_lib.XLHRatingBar;

import java.util.List;

public class CommentAdapter extends ArrayAdapter {
    private final int resourceId;

    public CommentAdapter(Context context, int textViewResourceId, List<Comment> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = (Comment) getItem(position); // 获取当前项的Comment实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        XLHRatingBar xlhRatingBar = (XLHRatingBar) view.findViewById(R.id.ratingBar);
        xlhRatingBar.setRatingView(new SimpleRatingView());
        xlhRatingBar.setRating(comment.getRate());
        xlhRatingBar.setEnabled(false);
        ImageView commentUserImage = (ImageView) view.findViewById(R.id.comment_image);//获取该布局内的图片视图
        TextView commentUsername = (TextView) view.findViewById(R.id.comment_username);//获取该布局内的文本视图
        TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
        TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
//        fruitImage.setImage;//为图片视图设置图片资源
        Glide.with(getContext()).load(Constant.baseUrl + "/files/original" +comment.getImagePath()).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)  .override(80,80).into(commentUserImage);
        commentUsername.setText(comment.getUsername());//为文本视图设置文本内容
        commentContent.setText(comment.getContent());
        commentDate.setText(comment.getDate());

        return view;
    }
}
