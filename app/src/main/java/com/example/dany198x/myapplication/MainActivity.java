package com.example.dany198x.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.example.dany198x.myapplication.Entity.Fruit;
import com.example.dany198x.myapplication.adapter.FruitAdapter;
import com.example.dany198x.myapplication.constant.Constant;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private List<Fruit> fruitList = new ArrayList<Fruit>();
    //为异步请求提供主线程入口
    private Handler handler = new Handler();
    private FruitAdapter adapter;
    XRefreshView xRefreshView;
    LinearLayout foodCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化refreshView
        xRefreshView = findViewById(R.id.xrefreshview_poetryFragment);

        //为下方美食列表设置adapter
        adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //为美食版块增加点击事件
        foodCell = (LinearLayout) findViewById(R.id.foodCell);
        foodCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, FoodListActivity.class);
                startActivity(intent);
            }
        });

        //前往个人中心，未登录则前往登陆，当前为无功能的静态页
        ImageView personal = findViewById(R.id.personal);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //为轮播赋值
        List<String> images = new ArrayList<>();
        images.add(Constant.baseUrl + "/files/original/11.jpg");
        images.add(Constant.baseUrl + "/files/original/12.jpg");
        images.add(Constant.baseUrl + "/files/original/13.jpg");
        List<String> titles = new ArrayList<String>();
        titles.add("好多的菜");
        titles.add("生意很火爆");
        titles.add("很好吃的样子");

        //调用轮播
        Banner banner = (Banner) findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        banner.setBannerTitles(titles);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        Log.d("myTag", "23321233213");
        //获取下方美食列表数据
        getFood();
        //设置上拉下拉事件
        setPullandRefresh();
        try {
            JSONObject jsb = new JSONObject();
            jsb.put("client_id", "1");
            jsb.put("client_secret", "1");
            jsb.put("grant_type", "password");
            jsb.put("password", "Dany-198x");
            jsb.put("scope", "1");
            jsb.put("username", "dany198x@163.com");
            Log.d("myTag", "33334444"+jsb);
            submit(jsb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void submit(JSONObject body) {
        MyService.sendPostRequest1("https://bots.kore.ai/api/1.1/oauth/token?rnd=wrmt5qqfgrlxwvfs9k9", body, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    Log.d("myTag", "444444"+jsonString);
                    JSONObject jsonObj = new JSONObject(jsonString);
                    Log.d("myTag", "555555"+jsonObj);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //调用美食列表接口
    private void getFood() {
        Log.d("myTag", "23321233213");
        MyService.sendGetRequest("/Hospital/listOnApp?pageStart=0&pageSize=5&districtName=ALL&typeName=ALL&sortType=", null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
            try {
                Log.d("myTag", "44444444");
                //获取响应数据为jsonString
                JSONObject jsonObj = new JSONObject(jsonString);
                final JSONArray jsonArray = new JSONArray(jsonObj.get("data").toString());
                //每次调用后清空原先list
                fruitList.clear();
                //为list赋值
                initFruits(jsonArray);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //通知主线程刷新页面
                        adapter.notifyDataSetChanged();
                        xRefreshView.stopRefresh();
                        xRefreshView.stopLoadMore();
                    }
                },1000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void setPullandRefresh() {
        xRefreshView.setPinnedTime(1000);
        //刷新时不让里面的列表滑动
        xRefreshView.setPinnedContent(true);
        xRefreshView.setMoveForHorizontal(true);
        //允许下拉刷新
        xRefreshView.setPullRefreshEnable(true);
        //允许上拉加载
        xRefreshView.setPullLoadEnable(false);
        xRefreshView.setAutoLoadMore(false);
//        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(false);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(false);
//        xRefreshView.setEmptyView(R.layout.layout_empty_view);//添加empty_view

        //设置上拉和下拉完成的监听事件
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            //下拉
            @Override
            public void onRefresh(boolean isPullDown) {
                //super.onRefresh(isPullDown);
                xRefreshView.setLoadComplete(false);
//                switch ((int) (Math.random() * 6)) {
//                    case 0:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_01));
//                        break;
//                    case 1:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_02));
//                        break;
//                    case 2:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_03));
//                        break;
//                    case 3:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_04));
//                        break;
//                    case 4:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_05));
//                        break;
//                    default:
//                        recommendHeadBg.setBackground(getActivity().getResources().getDrawable(R.drawable.index_banner_img_01));
//                }
                getFood();
            }

            //上拉
            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFood();
                    }
                }, 2000);
            }
        });
    }

    //下方美食列表赋值
    private void initFruits(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("myTag", i + "");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject pictureObject = (JSONObject) jsonObject.get("picture");
                JSONObject foodTypebject = (JSONObject) jsonObject.get("type");
                Log.d("myTag", "fruit" + Float.parseFloat(jsonObject.get("avgCost").toString()));
                //创建美食对象并加入list
                Fruit fr = new Fruit(jsonObject.get("id").toString(), jsonObject.get("title").toString(), pictureObject.get("filePath").toString(), Float.parseFloat(jsonObject.get("rate").toString()), Float.parseFloat(jsonObject.get("avgCost").toString()), jsonObject.get("address").toString(), foodTypebject.get("title").toString());
                fruitList.add(fr);
                Log.d("haha", String.join("",(String[])fruitList.toArray()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

