package com.houwei.guaishang.easemob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class DemoModel {
    protected Context context = null;
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    
    public DemoModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }
    
//    public boolean saveContactList(List<EaseUser> contactList) {
//        UserDao dao = new UserDao(context);
//        dao.saveContactList(contactList);
//        return true;
//    }

//    public Map<String, EaseUser> getContactList() {
//        UserDao dao = new UserDao(context);
//        return dao.getContactList();
//    }
    
//    public void saveContact(EaseUser user){
//        UserDao dao = new UserDao(context);
//        dao.saveContact(user);
//    }
    
    /**
     * 设置当前用户的环信id
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }
    
    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }
    
//    public Map<String, RobotUser> getRobotList(){
//        UserDao dao = new UserDao(context);
//        return dao.getRobotUser();
//    }
//
//    public boolean saveRobotList(List<RobotUser> robotList){
//        UserDao dao = new UserDao(context);
//        dao.saveRobotUser(robotList);
//        return true;
//    }
    
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void switchDisabledGroup(String groupId){
    	
    	 //根据当前登录的账号，获取他的不提醒群列表
      	SharedPreferences pref = context.getSharedPreferences(getCurrentUsernName()+"_DisabledGroups",Activity.MODE_PRIVATE);
      	Set<String> signArray = pref.getStringSet("DisabledGroups", new HashSet<String>());
        
      	if (signArray.contains(groupId)) {
      		signArray.remove(groupId);
		}else{
			signArray.add(groupId);
		}
      	
      	Editor editor =  pref.edit().clear();
		editor.putStringSet("DisabledGroups", signArray);
		editor.commit();
      	
        valueCache.put(Key.DisabledGroups, signArray);
    }
    
    public Set<String> getDisabledGroups(){
        Object val = valueCache.get(Key.DisabledGroups);

        if(val == null){
        	
            //根据当前登录的账号，获取他的不提醒群列表
          	SharedPreferences pref = context.getSharedPreferences(getCurrentUsernName()+"_DisabledGroups",Activity.MODE_PRIVATE);
          	Set<String> signArray = pref.getStringSet("DisabledGroups", new HashSet<String>());
        	
            valueCache.put(Key.DisabledGroups, signArray);
            return signArray;
        } else {
            return (Set<String>) val;
        }
    }
    
//    public void setDisabledIds(List<String> ids){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//        
//        dao.setDisabledIds(ids);
//        valueCache.put(Key.DisabledIds, ids);
//    }
//    
//    public List<String> getDisabledIds(){
//        Object val = valueCache.get(Key.DisabledIds);
//        
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledIds();
//            valueCache.put(Key.DisabledIds, val);
//        }
//       
//        return (List<String>) val;
//    }
//    
    public void setGroupsSynced(boolean synced){
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return PreferenceManager.getInstance().isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        PreferenceManager.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return PreferenceManager.getInstance().isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return PreferenceManager.getInstance().isBacklistSynced();
    }
    
    public void allowChatroomOwnerLeave(boolean value){
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }
    
    public boolean isChatroomOwnerLeaveAllowed(){
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }
   
    
    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
