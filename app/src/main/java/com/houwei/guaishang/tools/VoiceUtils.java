package com.houwei.guaishang.tools;

import android.content.Context;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * 百度语音工具类
 */

public class VoiceUtils {

//////////////////需配置部分////////////////////////
    //AppId
    private String APPID="11003766";
    //ApiKey
    private String APIKEY="MxSsRjTYYt2pWvR1M2njG4vt";
    //SecretKey
    private String SECRETKEY="c95395e4f5ae536db00629ac2a652721";
////////////////////配置结束////////////////////////////////////

    private static VoiceUtils instance;

    private static SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license.txt";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    // 发音人（在线引擎），可用参数为0,1,2,3。。。
    // （服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，4--情感女声，3--普通男声，6--特殊男声。。。）
    private int speaker = 4;

    //初始化
    public  void init(Context context){
        initialTts(context);
    }

    public static VoiceUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceUtils.class) {
                if (instance == null) {
                    instance = new VoiceUtils();
                    instance.initialTts(context);
                }
            }
        }
        return instance;
    }

    public void setSpeaker(int speaker) {
        this.speaker = speaker;
    }

    //获取解析器
    public SpeechSynthesizer getSyntheszer(){
        return mSpeechSynthesizer;
    }

    //初始化解析器
    private void initialTts(Context context) {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(context);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(new MyListnener());
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId(APPID);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey(APIKEY,
                SECRETKEY);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，4--情感女声，3--普通男声，6--特殊男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, speaker+"");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能)
        mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);

    }
    //合成监听器
    class MyListnener implements SpeechSynthesizerListener {
        @Override
        public void onSynthesizeStart(String s) {
            //合成准备工作
        }
        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            //合成数据和进度的回调接口，分多次回调
        }
        @Override
        public void onSynthesizeFinish(String s) {
            //合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
        }
        @Override
        public void onSpeechStart(String s) {
            //播放开始，每句播放开始都会回调
        }
        @Override
        public void onSpeechProgressChanged(String s, int i) {
            //播放进度回调接口，分多次回调
        }
        @Override
        public void onSpeechFinish(String s) {
            // 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
        }
        @Override
        public void onError(String s, SpeechError speechError) {
            //当合成或者播放过程中出错时回调此接口
        }
    }
}
