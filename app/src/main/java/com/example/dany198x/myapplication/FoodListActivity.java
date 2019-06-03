package com.example.dany198x.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

public class FoodListActivity extends Activity {

    @InjectView(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    XRefreshView xRefreshView;
    private List<Fruit> fruitList = new ArrayList<Fruit>();
    private Handler handler = new Handler();
    private FruitAdapter adapter;

    private String headers[] = {"地区", "分类", "排序"};
    private List<View> popupViews = new ArrayList<>();

    private GirdDropDownAdapter cityAdapter;
    private ListDropDownAdapter ageAdapter;
    private ListDropDownAdapter sexAdapter;

    private String citys[] = {"ALL", "长宁区", "徐汇区", "松江区", "奉贤区", "闵行区", "闸北区", "青浦区", "嘉定区", "杨浦区", "宝山区", "崇明县","金山区","虹口区","普陀区","卢湾区","黄浦区","静安区","浦东新区","南汇区"};
    private String ages[] = {"ALL", "本帮菜", "日本菜", "咖啡厅", "小吃快餐", "面包甜点","火锅","西餐","自助餐","粤菜","韩国料理","小龙虾","烧烤","川菜","东南亚菜","海鲜","新疆菜","湘菜","东北菜","西北菜","台湾菜"};
    private String sexs[] = {"不限", "价格升序","价格降序","评分升序","评分降序"};

    private int currentPage = 1;
    private String currentCity = "ALL";
    private String currentType = "ALL";
    private String currentSort = "";
    private String isEnd = "false";
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
                Log.d("myTag", "2132"+citys[position] + position);

                fruitList.clear();
                isEnd = "false";
                currentCity = citys[position];
                currentPage = 1;
                String url = "/Hospital/listOnApp?pageSize=10&pageStart=" + (currentPage - 1) * 10 + "&districtName=" + currentCity + "&typeName=" + currentType +"&sortType=" +  currentSort;
                Log.d("myTag", "the url is"+url);
                getFood(url);
                mDropDownMenu.closeMenu();

            }
        });

        ageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);

                fruitList.clear();
                isEnd = "false";
                currentType = ages[position];
                currentPage = 1;
                String url = "/Hospital/listOnApp?pageSize=10&pageStart=" + (currentPage - 1) * 10 + "&districtName=" + currentCity + "&typeName=" + currentType +"&sortType=" +  currentSort;
                Log.d("myTag", "the url is"+url);
                getFood(url);

                mDropDownMenu.closeMenu();
            }
        });

        sexView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sexAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[2] : sexs[position]);

                fruitList.clear();
                isEnd = "false";
                currentSort = position - 1 + "";
                currentPage = 1;
                String url = "/Hospital/listOnApp?pageSize=10&pageStart=" + (currentPage - 1) * 10 + "&districtName=" + currentCity + "&typeName=" + currentType +"&sortType=" +  currentSort;
                Log.d("myTag", "the url is"+url);
                getFood(url);

                mDropDownMenu.closeMenu();
            }
        });

        //init context view
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.activity_food_list, null);

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);

        xRefreshView = findViewById(R.id.xrefreshview_poetryFragment);
        adapter = new FruitAdapter(FoodListActivity.this, R.layout.fruit_item, fruitList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFood("/Hospital/listOnApp?pageStart=0&pageSize=10&districtName=ALL&typeName=ALL&sortType=");
        setPullandRefresh();
    }

    private void getFood(String url) {
        MyService.sendGetRequest(url, null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    final JSONArray jsonArray = new JSONArray(jsonObj.get("data").toString());
                    isEnd = jsonObj.get("isEnd").toString();
                    initFruits(jsonArray);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                            xRefreshView.stopRefresh();
                            xRefreshView.stopLoadMore();

                            if (isEnd.equals("true")) {
                                xRefreshView.setPullLoadEnable(false);
                            } else {
                                xRefreshView.setPullLoadEnable(true);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initFruits(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("myTag", i + "");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject pictureObject = (JSONObject) jsonObject.get("picture");
                JSONObject foodTypebject = (JSONObject) jsonObject.get("type");
                Log.d("myTag", "fruit" + pictureObject.get("filePath").toString());
                Fruit fr = new Fruit(jsonObject.get("id").toString(), jsonObject.get("title").toString(), pictureObject.get("filePath").toString(), Float.parseFloat(jsonObject.get("rate").toString()), Float.parseFloat(jsonObject.get("avgCost").toString()), jsonObject.get("address").toString(), foodTypebject.get("title").toString());
                fruitList.add(fr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage++;
                        String url = "/Hospital/listOnApp?pageSize=10&pageStart=" + (currentPage - 1) * 10 + "&districtName=" + currentCity + "&typeName=" + currentType +"&sortType=" +  currentSort;
                        Log.d("myTag", "the url is"+url);
                        getFood(url);
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
