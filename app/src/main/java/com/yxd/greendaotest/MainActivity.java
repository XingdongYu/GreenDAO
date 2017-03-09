package com.yxd.greendaotest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.yxd.greendaotest.gen.UserDao;
import com.yxd.greendaotest.helper.SimpleItemTouchHelperCallback;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private EditText etName;
    private EditText etAge;
    private EditText etSex;
    private Button btSubmit;
    private AutoCompleteTextView tvSearch;
    private RecyclerView mRecyclerView;
    private RxDao<User, Long> userDao;
    private RxQuery<User> userRxQuery;
    private Subscription mSubscription;
    private UsersAdapter mAdapter;
    private Observable<CharSequence> nameObservable;
    private Observable<CharSequence> ageObservable;
    private Observable<CharSequence> sexObservable;
    private ItemTouchHelper mItemTouchHelper;
    private Adapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        userDao = getUserDao().rx();
        userRxQuery = getUserDao().queryBuilder().orderAsc(UserDao.Properties.Id).rx();
        updateUsers();
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.et_name);
        etAge = (EditText) findViewById(R.id.et_age);
        etSex = (EditText) findViewById(R.id.et_sex);
        btSubmit = (Button) findViewById(R.id.bt_submit);
        tvSearch = (AutoCompleteTextView) findViewById(R.id.tv_search);
        tvSearch.setThreshold(1);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        if (mAdapter == null){
            mAdapter = new UsersAdapter(onItemDeletListener);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        nameObservable = RxTextView.textChanges(etName).skip(1);
        ageObservable = RxTextView.textChanges(etAge).skip(1);
        sexObservable = RxTextView.textChanges(etSex).skip(1);
        conbineLatestEvents();
    }

    /**
     * 输入是否合法判断，若不合法按键不可按
     */
    private void conbineLatestEvents() {
        mSubscription = Observable.combineLatest(nameObservable, ageObservable, sexObservable,
                new Func3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence name, CharSequence age, CharSequence sex) {
                        boolean nameValid = !isEmpty(name);
                        if (!nameValid){
                            etName.setError("名字不能为空");
                        }
                        boolean ageValid = !isEmpty(age);
                        if (ageValid){
                            int num = Integer.parseInt(age.toString());
                            ageValid = num > 0;
                        }
                        if (!ageValid){
                            etAge.setError("年龄须大于0");
                        }

                        boolean sexValid = !isEmpty(sex);
                        String mSex = sex.toString();
                        if (sexValid){
                            sexValid = "男".equals(mSex) ||
                                    "女".equals(mSex) ||
                                    "male".equals(mSex) ||
                                    "female".equals(mSex) ||
                                    "f".equals(mSex) ||
                                    "m".equals(mSex);
                        }
                        if (!sexValid){
                            etSex.setError("性别非法");
                        }

                        return nameValid && ageValid && sexValid;
                    }
                }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                btSubmit.setEnabled(aBoolean);
            }
        });
    }

    public void onClick(View v){
        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String sex = etSex.getText().toString();
        String date = getDate();
        User user = new User(null, name, Integer.parseInt(age), sex, date);
        userDao.insert(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                updateUsers();
            }
        });
    }

    /**
     * 更新数据
     */
    private void updateUsers() {
        userRxQuery.list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        mAdapter.setUsers(users);
                        itemAdapter = new Adapter(MainActivity.this, users);
                        tvSearch.setAdapter(itemAdapter);
                    }
                });
    }

    /**
     * 获取当前时间
     * @return
     */
    private String getDate(){
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.date));
        Date date = new Date();
        return df.format(date);
    }

    private UserDao getUserDao(){
        return GreenDaoManager.getInstance().getSession().getUserDao();
    }

    UsersAdapter.OnItemDeletListener onItemDeletListener = new UsersAdapter.OnItemDeletListener() {
        @Override
        public void onDelet(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)//这里context不能传getApplicationContext
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            User user = mAdapter.getUser(position);
                            final Long userId = user.getId();
                            userDao.deleteByKey(userId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            updateUsers();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateUsers();
                            dialog.dismiss();
                        }
                    })
                    .setMessage("是否删除当前内容？");
            builder.show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }
}
