package com.example.dany198x.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dany198x.myapplication.constant.Constant;
import com.example.dany198x.myapplication.myView.SmileRatingView;
import com.example.dany198x.myapplication.myView.BigRatingView;
import com.example.xlhratingbar_lib.XLHRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;


public class WriteCommentActivity extends Activity {
    public int mainRate = 0;
    public int tasteRate = 0;
    public int envRate = 0;
    public int serviceRate = 0;
    public String content = "";
    public String idString;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        idString = getIntent().getStringExtra("id");

        //声明四个打分条
        XLHRatingBar xlhRatingBar = (XLHRatingBar) findViewById(R.id.ratingBar_main);
        xlhRatingBar.setRatingView(new BigRatingView());

        XLHRatingBar xlhRatingBarTaste = (XLHRatingBar) findViewById(R.id.ratingBar_taste);
        xlhRatingBarTaste.setRatingView(new SmileRatingView());

        XLHRatingBar xlhRatingBarEnv = (XLHRatingBar) findViewById(R.id.ratingBar_env);
        xlhRatingBarEnv.setRatingView(new SmileRatingView());

        XLHRatingBar xlhRatingBarService = (XLHRatingBar) findViewById(R.id.ratingBar_service);
        xlhRatingBarService.setRatingView(new SmileRatingView());

        //声明4个打分描述
        final TextView mainRateDescView = findViewById(R.id.total_rate_desc);
        final TextView tasteRateDescView = findViewById(R.id.taste_rate_desc);
        final TextView envRateDescView = findViewById(R.id.env_rate_desc);
        final TextView serviceRateDescView = findViewById(R.id.service_rate_desc);
        //四个打分的监听
        xlhRatingBar.setOnRatingChangeListener(new XLHRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(float rating, int numStars) {
                mainRateDescView.setText(getRateDesc(rating));
                mainRate = (int)Math.ceil(rating);
            }
        });

        xlhRatingBarTaste.setOnRatingChangeListener(new XLHRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(float rating, int numStars) {
                tasteRateDescView.setText(getRateDesc(rating));
                tasteRate = (int)Math.ceil(rating);
            }
        });

        xlhRatingBarEnv.setOnRatingChangeListener(new XLHRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(float rating, int numStars) {
                envRateDescView.setText(getRateDesc(rating));
                envRate = (int)Math.ceil(rating);
            }
        });

        xlhRatingBarService.setOnRatingChangeListener(new XLHRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(float rating, int numStars) {
                serviceRateDescView.setText(getRateDesc(rating));
                serviceRate = (int)Math.ceil(rating);
            }
        });

        //监听评论输入
        final EditText commentContent = findViewById(R.id.comment_content);
        final TextView wordCount = findViewById(R.id.word_count);
        commentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = commentContent.getText().toString();
                wordCount.setText(content.length() + "/"
                        + 100);
            }
        });

        //监听价格输入
        final EditText avg_price = findViewById(R.id.avg_price);

        TextView submitComment = findViewById(R.id.submit_comment);
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(WriteCommentActivity.this, avg_price.getText().toString()+"-"+commentContent.getText()+"-"+mainRate+"-"+envRate+"-"+tasteRate+"-"+serviceRate, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsb = new JSONObject();
                    jsb.put("id", idString);
                    jsb.put("rate", mainRate);
                    jsb.put("cost", avg_price.getText());
                    jsb.put("content", commentContent.getText());
                    jsb.put("tasteRate", tasteRate);
                    jsb.put("envRate", envRate);
                    jsb.put("serviceRate", serviceRate);
                    submit(jsb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submit(JSONObject body) {
        MyService.sendPostRequest("/Comment/writeComment", body, new MyService.Callback() {
            @Override
            public void callback(String jsonString) {
                try {
                    Log.d("myTag", "2321312"+jsonString);
                    JSONObject jsonObj = new JSONObject(jsonString);
                    final String result = jsonObj.get("success").toString() == "true" ? "发表成功咯":"我擦，出事了";
                    final JSONObject jsonObj2 = new JSONObject(jsonObj.get("data").toString());
//                    Toast.makeText(WriteCommentActivity.this, jsonObj.get("success").toString(),Toast.LENGTH_LONG).show();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WriteCommentActivity.this, result,Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getRateDesc(float rate) {
        String result = "";
        switch((int)Math.ceil(rate)){
            case 1:
                result = Constant.rateDesc[0];
                break;
            case 2 :
                result = Constant.rateDesc[1];
                break;
            case 3 :
                result = Constant.rateDesc[2];
                break;
            case 4 :
                result = Constant.rateDesc[3];
                break;
            case 5 :
                result = Constant.rateDesc[4];
                break;
            default :
        }
        return result;
    }
}
