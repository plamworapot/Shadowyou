package com.shadowu.parent;

/**
 * Created by Ayzrl Skinwalker on 21/4/2558.
 */
public class ChildList {
    public String name;
    public String deviceId;
    public String object_id;
    ChildList(String name,String deviceId,String object_id){
        this.name = name;
        this.deviceId = deviceId;
        this.object_id = object_id;
    }
    ChildList(String name,String deviceId){
        this.name = name;
        this.deviceId = deviceId;
    }

}
