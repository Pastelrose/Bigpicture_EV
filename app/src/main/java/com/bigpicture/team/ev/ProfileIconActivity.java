package com.bigpicture.team.ev;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bigpicture.team.ev.item.MemberInfoItem;
import com.bigpicture.team.ev.lib.FileLib;
import com.bigpicture.team.ev.lib.MyLog;
import com.bigpicture.team.ev.lib.RemoteLib;
import com.bigpicture.team.ev.lib.StringLib;
import com.bigpicture.team.ev.remote.RemoteService;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfileIconActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = getClass().getSimpleName();

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int CROP_FROM_ALBUM = 3;

    Context context;
    ImageView profileIconImage;
    MemberInfoItem memberInfoItem;
    File profileIconFile;
    String profileIconFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_icon);

        context = this;
        memberInfoItem = ((MyApp)getApplication()).getMemberInfoItem();
        setToolbar();
        setView();
        setProfileIcon();
    }

    //액티비티 툴바 설정
    private void setToolbar(){
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_setting);
        }
    }

    //액티비티 화면 설정
    public void setView(){
        profileIconImage = (ImageView)findViewById(R.id.profile_icon);

        Button albumButton = (Button)findViewById(R.id.album);
        albumButton.setOnClickListener(this);

        Button cameraButton = (Button)findViewById(R.id.camera);
        cameraButton.setOnClickListener(this);
    }

    //프로필 아이콘 설정
    private void setProfileIcon(){
        MyLog.d(TAG,"onResume"+ RemoteService.MEMBER_ICON_URL+memberInfoItem.memberIconFilename);

        if(StringLib.getInstance().isBlank(memberInfoItem.memberIconFilename)){
            Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
        } else {
            Picasso.with(this).load(RemoteService.MEMBER_ICON_URL+memberInfoItem.memberIconFilename).into(profileIconImage);
        }
    }

    //사용자가 선택한 프로필 아이콘을 저장할 파일 이름을 설정
    private void setProfileIconFile(){
        profileIconFilename = memberInfoItem.seq + "_" + String.valueOf(System.currentTimeMillis());
        profileIconFile = FileLib.getInstance().getProfileIconFile(context,profileIconFilename);
    }

    //프로필 아이콘을 설정하기 위해 선택할 수 있는 앨범이나 카메라 버튼의 클릭 이벤트 설정
    //v : 클릭한 뷰 객체

    @Override
    public void onClick(View v) {
        setProfileIconFile();
        if(v.getId() == R.id.album){
            getImageFromAlbum();
        } else if(v.getId() == R.id.camera){
            getImageFromCamera();
        }
    }

    //우측 상단 메뉴 구성. 닫기 메뉴만 있는 menu_close.xml 지정
    //menu: 메뉴객체, 메뉴를 보여준다면 return true, 아니라면 return false

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    //좌측 화살표 메뉴(android.R.id.home)을 클릭했을때와 우측상단에서 닫기를 누를떄 동작
    //여기서 모든 액티비티 종료
    //item: 메뉴 아이템 객체
    //메뉴를 처리했다면 return true, 아니라면 false

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home : finish(); break;
            case R.id.action_close : finish(); break;
        }
        return true;
    }

    //카메라앱을 실행해서 이미지 촬영
    private void getImageFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(profileIconFile));
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //카메라 앨범을 실행해서 이미지 선택
    private void getImageFromAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    //이미지를 자르기위해 intent를 생성해서 반환
    //inputUri 이미지를 자르기 전 Uri
    //outputUri 이미지를 자른 결과 Uri
    //return 이미지를 자르기 위한 인텐트
    private Intent getCropIntent(Uri inputUri, Uri outputUri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri,"image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        return intent;
    }

    //카메라에서 촬영한 이미지를 프로필 아이콘에 사용할 크기로 자른다
    private void cropImageFromCamera(){
        Uri uri = Uri.fromFile(profileIconFile);
        Intent intent = getCropIntent(uri, uri);
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    //앨범에서 선택한 이미지를 프로필 아이콘에 사용할 크기로 자른다
    private void cropImageFromAlbum(Uri inputUri){
        Uri outputUri = Uri.fromFile(profileIconFile);

        MyLog.d(TAG, "startPickFromAlbum uri " + inputUri.toString());
        Intent intent = getCropIntent(inputUri, outputUri);
        startActivityForResult(intent, CROP_FROM_ALBUM);
    }

    //startActivityForResult()에서 호출한 액티비티의 결과를 처리
    //requestCode: 액티비티를 실행하면서 전달한 요청코드
    //resultCode: 실행한 액티비티가 설정한 결과코드 , intent: 결과 데이터

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        MyLog.d(TAG, "onActivityResult " +intent);
        if(resultCode != RESULT_OK)return;

        if(requestCode == PICK_FROM_CAMERA){
            cropImageFromCamera();
        } else if (requestCode == CROP_FROM_CAMERA) {
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadProfileIcon();
        } else if (requestCode == PICK_FROM_ALBUM && intent != null){
            Uri dataUri = intent.getData();
            if(dataUri != null){
                cropImageFromAlbum(dataUri);
            }
        } else if(requestCode == CROP_FROM_ALBUM && intent !=null){
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadProfileIcon();
        }
    }

    //프로필 아이콘을 서버에 업로드
    private void uploadProfileIcon(){
        RemoteLib.getInstance().uploadMemberIcon(memberInfoItem.seq, profileIconFile);
        memberInfoItem.memberIconFilename = profileIconFilename + ".png";
    }
}
