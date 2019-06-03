package com.example.dany198x.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dany198x.myapplication.Entity.Comment;
import com.example.dany198x.myapplication.Entity.Fruit;
import com.example.dany198x.myapplication.adapter.CommentAdapter;
import com.example.dany198x.myapplication.constant.Constant;
import com.example.dany198x.myapplication.myView.SimpleRatingView;
import com.example.xlhratingbar_lib.XLHRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;

public class FoodDetailsActivity extends Activity {
    XRefreshView xRefreshView;
    XScrollView xScrollView;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter adapter;
    private Handler handler = new Handler();
    String idString;
    private boolean firstStart = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        idString = getIntent().getStringExtra("id");
        Log.d("myTag", "222222" + idString);
        //设置初始透明度为0
        final RelativeLayout rl = findViewById(R.id.details_header);
        rl.getBackground().setAlpha(100);
        //获取弹动组件view
        xRefreshView = findViewById(R.id.xrefreshview_poetryFragment);
        xRefreshView.setPullRefreshEnable(false);
        xScrollView = findViewById(R.id.recommend_scrollView);
        //设置滚动监听
        xScrollView.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
                Log.d("myTag", "onScrollchange");
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                //随滚动改变header的透明度
                float percent = (float) (t) / 300f;
                if (t < 300) {
                    rl.getBackground().setAlpha((int) (155 * percent + 100));
                    Log.d("myTag", "onScroll" + (int) (255 * percent));
                } else {
                    rl.getBackground().setAlpha(255);
                }
                Log.d("myTag", "onScroll" + percent);
            }
        });

        //点评列表加载适配
        adapter = new CommentAdapter(FoodDetailsActivity.this, R.layout.comment_item, commentList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        getComment();

        //去评论
        ImageView writeComment = findViewById(R.id.write_comment);
        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FoodDetailsActivity.this, WriteCommentActivity.class);
                intent.putExtra("id",idString);
                startActivity(intent);
            }
        });

        //关闭
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //去全部评论列表
        LinearLayout viewMore = findViewById(R.id.view_more_comment);
        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FoodDetailsActivity.this, CommentListActivity.class);
                intent.putExtra("id",idString);
                startActivity(intent);
            }
        });

        getFood();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstStart == true) {
            firstStart = false;
        } else {
//            Toast.makeText(FoodDetailsActivity.this, "我回来啦",Toast.LENGTH_LONG).show();
            getComment();
            getFood();
        }
    }

    private void getFood() {
        MyService.sendGetRequest("/Hospital/getHospitalById?hospitalId="+idString, null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    final JSONObject jsonObj2 = new JSONObject(jsonObj.get("data").toString());


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initFood(jsonObj2);
                            adapter.notifyDataSetChanged();
                            xRefreshView.stopRefresh();
                            xRefreshView.stopLoadMore();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initFood(JSONObject jsonObj) {
        try {

            TextView header_title = findViewById(R.id.header_title);
            header_title.setText(jsonObj.get("title").toString());

            JSONObject jsb;
            TextView title = findViewById(R.id.title);
            title.setText(jsonObj.get("title").toString());

            JSONObject pictureObject = (JSONObject) jsonObj.get("picture");
            Log.d("myTag", "fruit1111" + pictureObject.get("filePath").toString());
            ImageView cover = (ImageView) findViewById(R.id.cover);
            Glide.with(FoodDetailsActivity.this).load(Constant.baseUrl + "/files/original" +pictureObject.get("filePath").toString()).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(cover);

            TextView comment_count = findViewById(R.id.comment_count);
            comment_count.setText(jsonObj.get("count").toString()+"条");

            final XLHRatingBar xlhRatingBar5 = (XLHRatingBar) findViewById(R.id.ratingBar1);
            xlhRatingBar5.setRatingView(new SimpleRatingView());
            xlhRatingBar5.setRating(Float.parseFloat(jsonObj.get("rate").toString()));
            xlhRatingBar5.setEnabled(false);

            TextView avg_cost = findViewById(R.id.avg_cost);
            avg_cost.setText("￥" +jsonObj.get("avgCost").toString()+"/人");

            TextView taste_rate = findViewById(R.id.taste_rate);
            taste_rate.setText("口味：" +jsonObj.get("tasteRate").toString());

            TextView env_rate = findViewById(R.id.env_rate);
            env_rate.setText("口味：" +jsonObj.get("envRate").toString());

            TextView service_rate = findViewById(R.id.service_rate);
            service_rate.setText("口味：" +jsonObj.get("serviceRate").toString());

            TextView food_type = findViewById(R.id.food_type);
            jsb = new JSONObject(jsonObj.get("foodType").toString());
            food_type.setText(jsb.get("title").toString());

            TextView district = findViewById(R.id.district);
            jsb = new JSONObject(jsonObj.get("district").toString());
            district.setText(jsb.get("name").toString());

            TextView address = findViewById(R.id.address);
            address.setText(jsonObj.get("address").toString());

            final String addressText = jsonObj.get("address").toString();

            LinearLayout address_box = findViewById(R.id.address_box);
            address_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(FoodDetailsActivity.this, MapActivity.class);
                    intent.putExtra("address",addressText);
                    startActivity(intent);
                }
            });

            final String phoneNo = jsonObj.get("contactNumber").toString();
            //电话按钮事件
            ImageView phone = findViewById(R.id.store_phone);
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.dialPhone(phoneNo, FoodDetailsActivity.this);
                }
            });

            TextView comment_count_title = findViewById(R.id.comment_count_title);
            comment_count_title.setText("网友点评（"+jsonObj.get("count").toString()+"）");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getComment() {
        MyService.sendGetRequest("/comment/list?id="+idString+"&pageSize=2&pageStart=0", null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                final JSONArray jsonArray = new JSONArray(jsonObj.get("data").toString());
                commentList.clear();
                initComments(jsonArray);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        xRefreshView.stopRefresh();
                        xRefreshView.stopLoadMore();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void initComments(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Comment comment = new Comment(jsonObject.get("username").toString(), jsonObject.get("avatar").toString(), Float.parseFloat(jsonObject.get("rate").toString()), jsonObject.get("content").toString(), jsonObject.get("dateCreated").toString());
                commentList.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
