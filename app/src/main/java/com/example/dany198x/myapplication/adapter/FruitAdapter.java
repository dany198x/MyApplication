package com.example.dany198x.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dany198x.myapplication.FoodDetailsActivity;
import com.example.dany198x.myapplication.Entity.Fruit;
import com.example.dany198x.myapplication.R;
import com.example.dany198x.myapplication.constant.Constant;
import com.example.dany198x.myapplication.myView.SimpleRatingView;
import com.example.xlhratingbar_lib.XLHRatingBar;

import java.util.List;


public class FruitAdapter extends ArrayAdapter{
    private final int resourceId;

    public FruitAdapter(Context context, int textViewResourceId, List<Fruit> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fruit fruit = (Fruit) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        final XLHRatingBar xlhRatingBar5 = (XLHRatingBar) view.findViewById(R.id.ratingBar5);
        xlhRatingBar5.setRatingView(new SimpleRatingView());
        xlhRatingBar5.setRating(fruit.getRate());
        xlhRatingBar5.setEnabled(false);
        ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);//获取该布局内的图片视图
        TextView fruitName = (TextView) view.findViewById(R.id.fruit_name);//获取该布局内的文本视图
        TextView fruit_addressType = (TextView) view.findViewById(R.id.fruit_addressType);
        TextView fruit_type = (TextView) view.findViewById(R.id.fruit_type);
        TextView avgCost = (TextView) view.findViewById(R.id.avgCost);
//        fruitImage.setImage;//为图片视图设置图片资源
        Glide.with(getContext()).load(Constant.baseUrl + "/files/original" +fruit.getImagePath()).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)  .override(80,80).into(fruitImage);
        fruitName.setText(fruit.getName());//为文本视图设置文本内容
        fruit_addressType.setText((fruit.getAddress()));
        fruit_type.setText(fruit.getType());
        avgCost.setText("￥" + fruit.getAvgCost() + "/人");
        final String fruitId = fruit.getId();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(),FoodDetailsActivity.class);
                intent.putExtra("id",fruitId);
                getContext().startActivity(intent);
            }
        });

        return view;
    }
}

