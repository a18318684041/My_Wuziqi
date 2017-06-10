package com.example.administrator.my_wuziqi;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyView view;
    private Button btn_restart;
    private Button btn_back;

    private TextView tv_num;
    private TextView tv_time;


    //计算玩家在棋盘上的总步数
    private int sum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_time = (TextView) findViewById(R.id.tv_time);
        view = (MyView) findViewById(R.id.view);
        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空棋盘，重新开始游戏
                view.clearqiZi();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.huiqi();
            }
        });
    }

    //进行横竖屏切换的监听
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            //当横竖屏切换的时候，重新绘制棋子
            //view.drawPieces();
            Log.d("AAA", "竖屏");
        }//切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            //当横竖屏切换的时候，重新绘制棋子
           // view.drawPieces();
            Log.d("AAA", "横屏");
        }
    }

    public  void settext(){
        sum++;
        tv_num.setText("总步数"+sum);
    }
}
