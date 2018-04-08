package com.houwei.guaishang.bean;

import com.houwei.guaishang.tools.HttpUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MyInfoBean {


    /**
     * code : 1
     * message : 成功
     * data : {"userid":"2","mobile":"13810240001","password":"123","name":"通知","avatar":{"original":"/media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png","small":"/media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png"},"age":"0","sex":"0","personalTags":null,"intro":null,"background":null,"createdAt":"2017-08-15 00:00:00","openid":"987654","license":null,"picture":[{"original":"/media/topic/photo/2017-10-20/1d37895bcaf53514.640_480.png","small":"/media/topic/photo/2017-10-20/1d37895bcaf53514.small.png"},{"original":"/media/topic/photo/2017-10-20/f96d1287bd20a0b0.600_900.png","small":"/media/topic/photo/2017-10-20/f96d1287bd20a0b0.small.png"}],"idCard":[{"original":"/media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png","small":"/media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png"}],"friendship":0,"followsCount":"3","fansCount":"1"}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * userid : 2
         * mobile : 13810240001
         * password : 123
         * name : 通知
         * avatar : {"original":"/media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png","small":"/media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png"}
         * age : 0
         * sex : 0
         * personalTags : null
         * intro : null
         * background : null
         * createdAt : 2017-08-15 00:00:00
         * openid : 987654
         * license : null
         * picture : [{"original":"/media/topic/photo/2017-10-20/1d37895bcaf53514.640_480.png","small":"/media/topic/photo/2017-10-20/1d37895bcaf53514.small.png"},{"original":"/media/topic/photo/2017-10-20/f96d1287bd20a0b0.600_900.png","small":"/media/topic/photo/2017-10-20/f96d1287bd20a0b0.small.png"}]
         * idCard : [{"original":"/media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png","small":"/media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png"}]
         * friendship : 0
         * followsCount : 3
         * fansCount : 1
         */

        private String userid;
        private String mobile;
        private String password;
        private String name;
        private AvatarBean avatar;
        private String age;
        private String sex;
        private Object personalTags;
        private Object intro;
        private Object background;
        private String createdAt;
        private String openid;
        private Object license;
        private int friendship;
        private String followsCount;
        private String fansCount;
        private List<PictureBean> picture;
        private List<IdCardBean> idCard;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AvatarBean getAvatar() {
            return avatar;
        }

        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Object getPersonalTags() {
            return personalTags;
        }

        public void setPersonalTags(Object personalTags) {
            this.personalTags = personalTags;
        }

        public Object getIntro() {
            return intro;
        }

        public void setIntro(Object intro) {
            this.intro = intro;
        }

        public Object getBackground() {
            return background;
        }

        public void setBackground(Object background) {
            this.background = background;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public Object getLicense() {
            return license;
        }

        public void setLicense(Object license) {
            this.license = license;
        }

        public int getFriendship() {
            return friendship;
        }

        public void setFriendship(int friendship) {
            this.friendship = friendship;
        }

        public String getFollowsCount() {
            return followsCount;
        }

        public void setFollowsCount(String followsCount) {
            this.followsCount = followsCount;
        }

        public String getFansCount() {
            return fansCount;
        }

        public void setFansCount(String fansCount) {
            this.fansCount = fansCount;
        }

        public List<PictureBean> getPicture() {
            return picture;
        }

        public void setPicture(List<PictureBean> picture) {
            this.picture = picture;
        }

        public List<IdCardBean> getIdCard() {
            return idCard;
        }

        public void setIdCard(List<IdCardBean> idCard) {
            this.idCard = idCard;
        }

        public static class AvatarBean {
            /**
             * original : /media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png
             * small : /media/topic/photo/2017-10-20/bad4ff1e692d8185f6672e7f54deb5dc.png
             */
            private String original;
            private String small;
            public String getOriginal() {
                if (original==null || original.startsWith("http") || original.equals("")) {
                    //为null 或者 “” 或者全路径。直接返回原始图片
                    return original;
                } else {
                    return HttpUtil.IP_NOAPI+original;
                }
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            public String getSmall() {
                if (small==null || small.startsWith("http") || small.equals("")) {
                    //为null 或者 “” 或者全路径。直接返回原始图片
                    return small;
                } else {
                    return HttpUtil.IP_NOAPI+small;
                }
            }

            public void setSmall(String small) {
                this.small = small;
            }
        }

        public static class PictureBean {
            /**
             * original : /media/topic/photo/2017-10-20/1d37895bcaf53514.640_480.png
             * small : /media/topic/photo/2017-10-20/1d37895bcaf53514.small.png
             */

            private String original;
            private String small;

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }
        }

        public static class IdCardBean {
            /**
             * original : /media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png
             * small : /media/topic/photo/2017-10-20/f6f488efbb51f9a1e02e1a0887e9b810.png
             */

            private String original;
            private String small;

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }
        }
    }
}
