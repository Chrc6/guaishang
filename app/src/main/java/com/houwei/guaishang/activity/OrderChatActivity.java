package com.houwei.guaishang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.houwei.guaishang.R;
import com.houwei.guaishang.adapter.OrderChatAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

public class OrderChatActivity extends BaseActivity implements View.OnClickListener,OrderChatAdapter.AdapterItemClickListener {

    private FrameLayout frameLayout;
    private RecyclerView mRecyclerView;
    private OrderChatAdapter mAdapter;
    private List list;

    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat);
        initData();
        initView();
    }

    private void initData() {
        list = new ArrayList();

        Intent intent = getIntent();
        orderId = intent.getLongExtra("orderId",0);

        //测试数据
        for(int i = 0; i < 20; i++) {
            UserBean userBean = new UserBean();
            AvatarBean bean = new AvatarBean();
            bean.setOriginal("https://www.baidu.com/img/bd_logo1.png");
            bean.setSmall("https://www.baidu.com/img/bd_logo1.png");
            userBean.setAvatar(bean);
            userBean.setName("测试数据"+i);
            list.add(userBean);
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        //创建LinearLayoutManager 对象 这里使用 <span style="font-family:'Source Code Pro';">LinearLayoutManager 是线性布局的意思</span>
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        //设置RecyclerView 布局
        mRecyclerView.setLayoutManager(layoutmanager);
        //设置Adapter
        mAdapter = new OrderChatAdapter(list);
        mAdapter.setItemOnclickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        frameLayout = (FrameLayout) findViewById(R.id.fl_container);

        findViewById(R.id.ll_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onClick(String userId) {
        //fragment聊天页面 切换
    }
}
