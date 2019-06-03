package com.example.dany198x.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.andview.refreshview.XRefreshView;
import com.example.dany198x.myapplication.Entity.Comment;
import com.example.dany198x.myapplication.Entity.Fruit;
import com.example.dany198x.myapplication.adapter.CommentAdapter;
import com.example.dany198x.myapplication.adapter.FruitAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentListActivity extends Activity {
    XRefreshView xRefreshView;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter adapter;
    private Handler handler = new Handler();
    String idString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        idString = getIntent().getStringExtra("id");

        xRefreshView = findViewById(R.id.xrefreshview_poetryFragment);
        adapter = new CommentAdapter(CommentListActivity.this, R.layout.comment_item, commentList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getComment();
        setPullandRefresh();
    }

    private void getComment() {
        MyService.sendGetRequest("/comment/list?id="+idString+"&pageSize=10&pageStart=0", null, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    final JSONArray jsonArray = new JSONArray(jsonObj.get("data").toString());
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
                Log.d("myTag", i + "");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Comment comment = new Comment(jsonObject.get("username").toString(), jsonObject.get("avatar").toString(), Float.parseFloat(jsonObject.get("rate").toString()), jsonObject.get("content").toString(), jsonObject.get("dateCreated").toString());
                commentList.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setScrollListener() {
        xRefreshView.setOnAbsListViewScrollListener(new AbsListView.OnScrollListener() {
            private SparseArray recordSp = new SparseArray(0);
            private int mCurrentfirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView
                                                     view, int scrollState) {
                Log.d("myTag", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                mCurrentfirstVisibleItem = arg1;
                View firstView = arg0.getChildAt(0);
                if (null != firstView) {
                    ItemRecod itemRecord = (ItemRecod) recordSp.get(arg1);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecod();
                    }
                    itemRecord.height = firstView.getHeight();
                    itemRecord.top = firstView.getTop();
                    recordSp.append(arg1, itemRecord);
                    int h = getScrollY();//滚动距离
                    float percent = (float) (h) / 300f;
                    if (h < 300) {
//                        rl.setAlpha(percent);
                        Log.d("myTag", "onScroll" + percent);
                    } else {
//                        rl.setAlpha(1);
                    }

                    Log.d("myTag", "onScroll" + h);
                }
            }

            private int getScrollY() {
                int height = 0;
                for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
                    ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
                    height += itemRecod.height;
                }
                ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }


            class ItemRecod {
                int height = 0;
                int top = 0;
            }
        });
    }

    private void setPullandRefresh() {
        xRefreshView.setPinnedTime(1000);
        //如果刷新时不想让里面的列表滑动，可以这么设置
        xRefreshView.setPinnedContent(false);
        xRefreshView.setMoveForHorizontal(true);
        //允许下拉刷新
        xRefreshView.setPullRefreshEnable(false);
        xRefreshView.setPullLoadEnable(false);
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
                getComment();
//                xRefreshView.stopRefresh();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getComment();
                    }
                }, 2000);
            }
        });
    }
}
