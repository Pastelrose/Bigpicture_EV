package com.bigpicture.team.ev.item;

import com.google.gson.annotations.SerializedName;

/**
 * 충전소 정보를 저장하는 객체
 */
@org.parceler.Parcel
public class ESCInfoItem {
    public String addr;
    public int cpId;
    public int cpTp;
    public String cpNm;
    public int chargeTp;
    public int csId;
    public String csNm;
    public double lat;
    public double lon;
    @SerializedName("reg_date") public String regDate;
    @SerializedName("mod_date") public String modDate;
    @SerializedName("user_distance_meter") public double userDistanceMeter;
    @SerializedName("image_filename") public String imageFilename;

    @Override
    public String toString() {
        return "ESCInfoItem{" +
                "cpId=" + cpId +
                ", cpTp=" + cpTp +
                ", cpNm='" + cpNm + '\'' +
                ", chargeTp='" + chargeTp + '\'' +
                ", addr='" + addr + '\'' +
                ", csId=" + csId +
                ", csNm=" + csNm +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", regDate='" + regDate + '\'' +
                ", modDate='" + modDate + '\'' +
                ", userDistanceMeter=" + userDistanceMeter +
                ", imageFilename='" + imageFilename + '\'' +
                '}';
    }
}
