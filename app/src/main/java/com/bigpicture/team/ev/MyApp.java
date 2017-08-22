package com.bigpicture.team.ev;

import android.app.Application;
import android.os.StrictMode;

import com.bigpicture.team.ev.item.ESCInfoItem;
import com.bigpicture.team.ev.item.MemberInfoItem;

/**
 * Created by NuuN on 2017-08-22.
 */

public class MyApp extends Application{
    private MemberInfoItem memberInfoItem;
    private ESCInfoItem escInfoItem;

    @Override
    public void onCreate() {
        super.onCreate();
        //FileUriExposedException은 문제 해결을 위한 코드
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public MemberInfoItem getMemberInfoItem(){
        if (memberInfoItem == null) memberInfoItem = new MemberInfoItem();
        return memberInfoItem;
    }

    public void setMemberInfoItem(MemberInfoItem item){
        this.memberInfoItem = item;
    }

    public int getMemberSeq(){
        return memberInfoItem.seq;
    }

    public void setEscInfoItem(ESCInfoItem escInfoItem){
        this.escInfoItem = escInfoItem;
    }

    public ESCInfoItem getEscInfoItem(){
        return escInfoItem;
    }
}
