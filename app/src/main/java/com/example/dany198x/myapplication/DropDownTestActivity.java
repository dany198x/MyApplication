package com.example.dany198x.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.example.dany198x.myapplication.Entity.Fruit;
import com.example.dany198x.myapplication.adapter.FruitAdapter;
import com.example.dany198x.myapplication.adapter.GirdDropDownAdapter;
import com.example.dany198x.myapplication.adapter.ListDropDownAdapter;
import com.yyydjk.library.DropDownMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.baidu.mapapi.BMapManager.getContext;


public class DropDownTestActivity extends AppCompatActivity {


    @InjectView(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    XRefreshView xRefreshView;
    private List<Fruit> fruitList = new ArrayList<Fruit>();
    private Handler handler = new Handler();
    private FruitAdapter adapter;

    private String headers[] = {"城市", "年龄", "性别"};
    private List<View> popupViews = new ArrayList<>();

    private GirdDropDownAdapter cityAdapter;
    private ListDropDownAdapter ageAdapter;
    private ListDropDownAdapter sexAdapter;

    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
    private String sexs[] = {"不限", "男", "女"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_down_test);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        //init city menu
        final ListView cityView = new ListView(this);
        cityAdapter = new GirdDropDownAdapter(this, Arrays.asList(citys));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        //init age menu
        final ListView ageView = new ListView(this);
        ageView.setDividerHeight(0);
        ageAdapter = new ListDropDownAdapter(this, Arrays.asList(ages));
        ageView.setAdapter(ageAdapter);

        //init sex menu
        final ListView sexView = new ListView(this);
        sexView.setDividerHeight(0);
        sexAdapter = new ListDropDownAdapter(this, Arrays.asList(sexs));
        sexView.setAdapter(sexAdapter);

        //init popupViews
        popupViews.add(cityView);
        popupViews.add(ageView);
        popupViews.add(sexView);

        //add item click event
        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : citys[position]);
                mDropDownMenu.closeMenu();
            }
        });

        ageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);
                mDropDownMenu.closeMenu();
            }
        });

        sexView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sexAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[2] : sexs[position]);
                mDropDownMenu.closeMenu();
            }
        });

        //init context view
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.activity_food_list, null);

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);

        xRefreshView = findViewById(R.id.xrefreshview_poetryFragment);
        adapter = new FruitAdapter(DropDownTestActivity.this, R.layout.fruit_item, fruitList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        getFood();
        setPullandRefresh();
    }

    private void getFood() {
        MyService.sendGetRequest("/hospital/list/?random=668&city=%E5%85%A8%E9%83%A8&cityId=0&level=0&pageSize=10&pageStart=0", null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    final JSONArray jsonArray = new JSONArray(jsonObj.get("data").toString());
                    initFruits(jsonArray);

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

    private void initFruits(JSONArray jsonArray) {

    }

    private void setPullandRefresh() {
        xRefreshView.setPinnedTime(1000);
        //如果刷新时不想让里面的列表滑动，可以这么设置
        xRefreshView.setPinnedContent(false);
        xRefreshView.setMoveForHorizontal(true);
        //允许下拉刷新
        xRefreshView.setPullRefreshEnable(false);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
//        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(false);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(false);
//        xRefreshView.setEmptyView(R.layout.layout_empty_view);//添加empty_view

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

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
//                executeTask(mService.getSlideshow(10+""),"Slideshow");//轮播图
//                executeTask(mService.getRecommendHear(MainApplication.getInstance().getUserInfo().getUserIdentifier()), "everydayHear");
                getFood();
//                xRefreshView.stopRefresh();
            }

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

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
