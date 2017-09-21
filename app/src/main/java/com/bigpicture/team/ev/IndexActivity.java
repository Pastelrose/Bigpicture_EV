package com.bigpicture.team.ev;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bigpicture.team.ev.item.MemberInfoItem;
import com.bigpicture.team.ev.lib.EtcLib;
import com.bigpicture.team.ev.lib.GeoLib;
import com.bigpicture.team.ev.lib.MyLog;
import com.bigpicture.team.ev.lib.RemoteLib;
import com.bigpicture.team.ev.lib.StringLib;
import com.bigpicture.team.ev.remote.RemoteService;
import com.bigpicture.team.ev.remote.ServiceGenerator;

import java.lang.reflect.Member;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//시작 액티비티, 사용자 정보를 통하여 메인으로 갈지 프로필로 갈지 결정한다
public class IndexActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    Context context;

    //인터넷 연결확인
    //인터넷이 연결되어있지 않다면 showNoService()호출
    //saveInstanceState: 액티비티가 새로 생성되었을 경우에 이전 상태값을 갖는 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //context = this;

        if (!RemoteLib.getInstance().isConnected(this)) {
            showNoService();
            return;
        }
    }


    //0.5초 이후에 startTask() 호출하여 서버에서 사용자 정보 조회
    @Override
    protected void onStart() {
        super.onStart();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTask();
            }
        },500);
    }

    //인터넷 접속 불가 메세지와 함께 종료 버튼
    private void showNoService(){
        TextView messageText = (TextView)findViewById(R.id.message);
        messageText.setVisibility(View.VISIBLE);

        Button closeButton = (Button)findViewById(R.id.close);
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //ID와 동일한 사용자 조회하기 위해 selectMemberInfo()호출,
    //setLastKnownLocation() 호출하여 현재 위치 설정
    public void startTask(){
        String phone_num = EtcLib.getInstance().getPhoneNumber(this);

        //phone번호 확인
        //Toast.makeText(IndexActivity.this, userId,Toast.LENGTH_LONG).show();
        selectMemberInfo(phone_num);
        GeoLib.getInstance().setLastKnownLocation(this);
    }

    //레트로핏을 사용하여 폰번호로 사용자 정보 조회. 조회성공시 setMemberInfoItem호출
    //실패시에 goProfileactivity()호출
    public void selectMemberInfo(String phone_num){
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);
        Call<MemberInfoItem> call = remoteService.selectMemberInfo(phone_num);

        call.enqueue(new Callback<MemberInfoItem>() {
            @Override
            public void onResponse(Call<MemberInfoItem> call, Response<MemberInfoItem> response) {
                MemberInfoItem item = response.body();
                if(response.isSuccessful() && !StringLib.getInstance().isBlank(item.phone_num)){
                    MyLog.d(TAG,"success "+ response.body().toString());
                    setMemberInfoItem(item);
                }
                else{
                    MyLog.d(TAG,"not success");
                    //Toast.makeText(IndexActivity.this, "not success",Toast.LENGTH_LONG).show();
                    goProfileActivity(item);
                }
            }

            @Override
            public void onFailure(Call<MemberInfoItem> call, Throwable t) {
                MyLog.d(TAG,"no internet connectivity");
                MyLog.d(TAG,t.toString());
            }
        });
    }

    //전달받은 MemberInfoItem을 application 객체에 저장하고 startMain() 메소드 호출
    //item: 사용자 정보
    private void setMemberInfoItem(MemberInfoItem item){
        ((MyApp) getApplicationContext()).setMemberInfoItem(item);
        startMain();
    }

    //메인액티비티를 실행하고 현재 액티비티 종료
    public void startMain(){
        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    //사용자 정보를 조회하지 못했을때
    //MainActivity 실행한 후에 ProfileActivity 실행
    //그리고 현재 액티비티 종료
    private void goProfileActivity(MemberInfoItem item){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        Intent intent2 = new Intent(this, ProfileActivity.class);
        startActivity(intent2);

        finish();
    }

    //폰번호를 서버에 저장
//    private void insertMemberPhone(){
//        String phone = EtcLib.getInstance().getPhoneNumber(this);
//        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);
//
//        Call<String> call = remoteService.insertMemberPhone(phone);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if(response.isSuccessful()){
//                    MyLog.d(TAG,"success insert id"+response.body().toString());
//                }else{
//                    int statusCode = response.code();
//
//                    ResponseBody errorBody = response.errorBody();
//                    MyLog.d(TAG,"fail"+statusCode+errorBody.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                MyLog.d(TAG,"no internet connectivity");
//            }
//        });
//    }
}
