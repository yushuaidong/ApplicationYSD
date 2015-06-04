package com.machineVision.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.machineVision.R;
import com.machineVision.otg_communication_service.OtgCommunicationService;
import com.machineVision.otg_communication_service.OtgReceiveData;
import com.machineVision.otg_communication_service.OtgSendData;

import java.lang.ref.WeakReference;

/**
 * Created by ysd on 2015/5/14.
 */
public class machineVisionActivity extends OtgBaseActivity implements View.OnClickListener {

    private Button set1_1, set1_2, set1_3, set1_4, set1_5, set1_6, set1_7, set1_8, set1_9;
    private Button set2_1, set2_2, set2_3, set2_4, set2_5, set2_6, set2_7, set2_8, set2_9;
    private Button set3_1, set3_2, set3_3, set3_4, set3_5, set3_6, set3_7, set3_8, set3_9;
    private Button set4_1, set4_2, set4_3, set4_4, set4_5, set4_6, set4_7, set4_8, set4_9;
    private Button set5_1, set5_2, set5_3, set5_4, set5_5, set5_6, set5_7, set5_8, set5_9;
    private Button set6_1, set6_2, set6_3, set6_4, set6_5, set6_6, set6_7, set6_8, set6_9;
    private Button set7_1, set7_2, set7_3, set7_4, set7_5, set7_6, set7_7, set7_8, set7_9;
    private Button set8_1, set8_2, set8_3, set8_4, set8_5, set8_6, set8_7, set8_8, set8_9;
    private Button set9_1, set9_2, set9_3, set9_4, set9_5, set9_6, set9_7, set9_8, set9_9;

//    //按键矩阵
//    private Button[][] matrixButton = {
//            {set1_1, set1_2, set1_3, set1_4, set1_5, set1_6, set1_7, set1_8, set1_9},
//            {set2_1, set2_2, set2_3, set2_4, set2_5, set2_6, set2_7, set2_8, set2_9},
//            {set3_1, set3_2, set3_3, set3_4, set3_5, set3_6, set3_7, set3_8, set3_9},
//            {set4_1, set4_2, set4_3, set4_4, set4_5, set4_6, set4_7, set4_8, set4_9},
//            {set5_1, set5_2, set5_3, set5_4, set5_5, set5_6, set5_7, set5_8, set5_9},
//            {set6_1, set6_2, set6_3, set6_4, set6_5, set6_6, set6_7, set6_8, set6_9},
//            {set7_1, set7_2, set7_3, set7_4, set7_5, set7_6, set7_7, set7_8, set7_9},
//            {set8_1, set8_2, set8_3, set8_4, set8_5, set8_6, set8_7, set8_8, set8_9},
//            {set9_1, set9_2, set9_3, set9_4, set9_5, set9_6, set9_7, set9_8, set9_9},
//    };

    private Button btn_simulate_set,btn_getOK,btn_Cancel;
    private Button btn_simulate_select;



    private String SimulateStr = "0100000001000000040000000400000011000000110000001100000011000000220000002200000022000000220000003300000033000000330000003300000044000000440000004400000044000000";

    private String strAllData;

    //otg 通信服务
    private OtgCommunicationService mOtgCommunicationService;
    private MyServiceConn conn;
    private OtgDataReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_vision);

        findView();
        //setViewVisibility();
        btnSetOnclik();

        //绑定Otg Service
        conn = new MyServiceConn();
        bindService(new Intent(machineVisionActivity.this, OtgCommunicationService.class), conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        doRegisterReceiver();  //注册广播接收器
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver!=null) { //解绑broadcast receiver
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(conn);  //解绑Service
    }



    /**
     * 上面的代码activity绑定了一个Service,上面需要一个ServiceConnection对象,使用匿名内部类
     */
    public final class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个OtgSrevice对象
            mOtgCommunicationService = ((OtgCommunicationService.OtgMsgBind)service).getService();
            mOtgCommunicationService.setOtgModule(myOtgModule);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mOtgCommunicationService = null;
        }
    }

    /**
     *  发送otg读消息
     */
    protected void otgSendReadData(OtgSendData.reqType req_type){
        myOtgModule.openUsbSerial();
        mOtgCommunicationService.ServiceSendReadOtgData(req_type);
    }

    /**
     *  发送otg写消息
     */
    protected void otgSendWriteData(OtgSendData.reqType req_type, String wirte_otg_data){
        myOtgModule.openUsbSerial();
        mOtgCommunicationService.ServiceSendWriteOtgData(req_type, wirte_otg_data);
    }



    /**
     * 注册广播接收器
     */
    private void doRegisterReceiver() {
        mReceiver=new OtgDataReceiver();
        IntentFilter filter = new IntentFilter("com.machineVision.otg_communication_service.OtgCommunicationService_Broadcast");
        registerReceiver(mReceiver, filter);
    }

    /**
     *  otg 数据 广播接收器
     */
    public final class OtgDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();     //得到服务接收、解析后的数据

            Message otgMessage = Message.obtain();      //由Message上传Bundle信息
            otgMessage.setData(bundle);
            otgMessage.what = getMessageWhat(bundle);   //由bundle值得出what值

            mActivityLoginHandler.sendMessage(otgMessage);   //发送需要处理的Message消息

        }
    }


    /**
     * 由bundle值得出what值
     */
    public int getMessageWhat(final Bundle bundle){
        if (bundle.getInt("receiveDataResultType") == OtgReceiveData.receiveDataResultType.RECEIVE_DATA_RESULT_NORMAL.ordinal()) {
            int order = bundle.getInt("respType");
            if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_TEMPLATE_INPUT_RESP.ordinal()) {
                return 1;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_TEMPLATE_REMOVE_RESP_RESP.ordinal()) {
                return 2;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_TEMPLATE_INPUT_START_RESP.ordinal()) {
                return 3;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_TEMPLATE_REMOVE_START_RESP.ordinal()) {
                return 4;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_BACK_RESP.ordinal()) {
                return 5;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_ENSURE_RESP.ordinal()) {
                return 6;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_CANCEL_RESP.ordinal()) {
                return 7;
            }
            else if (order == OtgReceiveData.respType.MACHINE_VISION_BTN_TEMPLATE_INPUT_SUCCESS_RESP.ordinal()) {
                return 8;
            }
            return 1000;

        }
        else if (bundle.getInt("receiveDataResultType") == OtgReceiveData.receiveDataResultType.RECEIVE_DATA_RESULT_NUll.ordinal()){
            return 2000;
        }
        else if (bundle.getInt("receiveDataResultType") == OtgReceiveData.receiveDataResultType.RECEIVE_DATA_RESULT_NO_START_CODE.ordinal()){
            return 3000;
        }
        else if (bundle.getInt("receiveDataResultType") == OtgReceiveData.receiveDataResultType.RECEIVE_DATA_RESULT_WRONG_RESP_TYPE.ordinal()){
            return 4000;
        }

        return 0;
    }




    /**
     *   handler处理函数    handler不应包括外部类的隐式引用
     *   Service或Activity中的增加一个内部static Handler类，这个内部类持有Service或Activity的弱引用
     */
    private ActivityLoginHandler mActivityLoginHandler = new ActivityLoginHandler(this);
    static class ActivityLoginHandler extends Handler {
        //得到activity的弱引用
        WeakReference<machineVisionActivity> mActivity;
        ActivityLoginHandler(machineVisionActivity activity) {
            mActivity = new WeakReference<machineVisionActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            machineVisionActivity theActivity = mActivity.get();
            Bundle bundle = msg.getData();  //接收由Message上传上来的Otg数据信息

            theActivity.strAllData = bundle.getString("allOtgData");


            switch (msg.what){
                case 0:
                    theActivity.showOtgCommunicationDialog(theActivity.dialogViewBundle.getString(TASK), "返回结果为0");
                    break;

                case 1:

                    break;

                case 2:

                    break;

                case 3:
                    String str = bundle.getString("data");
                    if(str == null){
                        theActivity.showOtgCommunicationDialog("测试返回数据信息", "开始录入");
                    }else {
                        theActivity.showOtgCommunicationDialog("测试返回数据信息", str);
                    }

                    break;

                case 4:

                    break;

                case 5:

                    break;

                case 6:

                    break;

                case 7:

                    break;

                case 8:

                    break;

                case 1000:
                    theActivity.showOtgCommunicationDialog(theActivity.dialogViewBundle.getString(TASK), "没有存在类型1000");
                    break;

                case 2000:
                    theActivity.showOtgCommunicationDialog(theActivity.dialogViewBundle.getString(TASK), "没有收到数据2000");
                    break;

                case 3000:
                    theActivity.showOtgCommunicationDialog(theActivity.dialogViewBundle.getString(TASK), "没有正确的起始码3000");
                    break;

                case 4000:
                    theActivity.showOtgCommunicationDialog(theActivity.dialogViewBundle.getString(TASK), "没有正确类型4000");
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };




    /**
     *  按键按键响应
     */
    protected void btnSetOnclik(){

        btn_simulate_set.setOnClickListener(this);
        btn_getOK.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);

//        for(int i = 0; i < 9; i++){
//            for(int j = 0; j < 9; j++){
//                matrixButton[i][j].setOnClickListener(this);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_simulate_set:
                otgSendWriteData(OtgSendData.reqType.MACHINE_VISION_BTN_TEMPLATE_INPUT_START,SimulateStr);
                showOtgCommunicationDialog("模拟发送模板录入信息", "正在发送");
                break;

            case R.id.btn_getOK:
                break;

            case R.id.btn_Cancel:
                break;

            case R.id.btn_simulate_select:

                break;



            default:
                break;
        }
    }


    protected void findView() {
        btn_simulate_set = (Button)findViewById(R.id.btn_simulate_set);
        btn_getOK = (Button)findViewById(R.id.btn_getOK);
        btn_Cancel = (Button)findViewById(R.id.btn_Cancel);
        btn_simulate_select = (Button)findViewById(R.id.btn_simulate_select);

//        set1_1 = (Button)findViewById(R.id.set1_1);
//        set1_2 = (Button)findViewById(R.id.set1_2);
//        set1_3 = (Button)findViewById(R.id.set1_3);
//        set1_4 = (Button)findViewById(R.id.set1_4);
//        set1_5 = (Button)findViewById(R.id.set1_5);
//        set1_6 = (Button)findViewById(R.id.set1_6);
//        set1_7 = (Button)findViewById(R.id.set1_7);
//        set1_8 = (Button)findViewById(R.id.set1_8);
//        set1_9 = (Button)findViewById(R.id.set1_9);
//
//        set2_1 = (Button)findViewById(R.id.set2_1);
//        set2_2 = (Button)findViewById(R.id.set2_2);
//        set2_3 = (Button)findViewById(R.id.set2_3);
//        set2_4 = (Button)findViewById(R.id.set2_4);
//        set2_5 = (Button)findViewById(R.id.set2_5);
//        set2_6 = (Button)findViewById(R.id.set2_6);
//        set2_7 = (Button)findViewById(R.id.set2_7);
//        set2_8 = (Button)findViewById(R.id.set2_8);
//        set2_9 = (Button)findViewById(R.id.set2_9);
//
//        set3_1 = (Button)findViewById(R.id.set3_1);
//        set3_2 = (Button)findViewById(R.id.set3_2);
//        set3_3 = (Button)findViewById(R.id.set3_3);
//        set3_4 = (Button)findViewById(R.id.set3_4);
//        set3_5 = (Button)findViewById(R.id.set3_5);
//        set3_6 = (Button)findViewById(R.id.set3_6);
//        set3_7 = (Button)findViewById(R.id.set3_7);
//        set3_8 = (Button)findViewById(R.id.set3_8);
//        set3_9 = (Button)findViewById(R.id.set3_9);
//
//        set4_1 = (Button)findViewById(R.id.set4_1);
//        set4_2 = (Button)findViewById(R.id.set4_2);
//        set4_3 = (Button)findViewById(R.id.set4_3);
//        set4_4 = (Button)findViewById(R.id.set4_4);
//        set4_5 = (Button)findViewById(R.id.set4_5);
//        set4_6 = (Button)findViewById(R.id.set4_6);
//        set4_7 = (Button)findViewById(R.id.set4_7);
//        set4_8 = (Button)findViewById(R.id.set4_8);
//        set4_9 = (Button)findViewById(R.id.set4_9);
//
//        set5_1 = (Button)findViewById(R.id.set5_1);
//        set5_2 = (Button)findViewById(R.id.set5_2);
//        set5_3 = (Button)findViewById(R.id.set5_3);
//        set5_4 = (Button)findViewById(R.id.set5_4);
//        set5_5 = (Button)findViewById(R.id.set5_5);
//        set5_6 = (Button)findViewById(R.id.set5_6);
//        set5_7 = (Button)findViewById(R.id.set5_7);
//        set5_8 = (Button)findViewById(R.id.set5_8);
//        set5_9 = (Button)findViewById(R.id.set5_9);
//
//        set6_1 = (Button)findViewById(R.id.set6_1);
//        set6_2 = (Button)findViewById(R.id.set6_2);
//        set6_3 = (Button)findViewById(R.id.set6_3);
//        set6_4 = (Button)findViewById(R.id.set6_4);
//        set6_5 = (Button)findViewById(R.id.set6_5);
//        set6_6 = (Button)findViewById(R.id.set6_6);
//        set6_7 = (Button)findViewById(R.id.set6_7);
//        set6_8 = (Button)findViewById(R.id.set6_8);
//        set6_9 = (Button)findViewById(R.id.set6_9);
//
//        set7_1 = (Button)findViewById(R.id.set7_1);
//        set7_2 = (Button)findViewById(R.id.set7_2);
//        set7_3 = (Button)findViewById(R.id.set7_3);
//        set7_4 = (Button)findViewById(R.id.set7_4);
//        set7_5 = (Button)findViewById(R.id.set7_5);
//        set7_6 = (Button)findViewById(R.id.set7_6);
//        set7_7 = (Button)findViewById(R.id.set7_7);
//        set7_8 = (Button)findViewById(R.id.set7_8);
//        set7_9 = (Button)findViewById(R.id.set7_9);
//
//        set8_1 = (Button)findViewById(R.id.set8_1);
//        set8_2 = (Button)findViewById(R.id.set8_2);
//        set8_3 = (Button)findViewById(R.id.set8_3);
//        set8_4 = (Button)findViewById(R.id.set8_4);
//        set8_5 = (Button)findViewById(R.id.set8_5);
//        set8_6 = (Button)findViewById(R.id.set8_6);
//        set8_7 = (Button)findViewById(R.id.set8_7);
//        set8_8 = (Button)findViewById(R.id.set8_8);
//        set8_9 = (Button)findViewById(R.id.set8_9);
//
//        set9_1 = (Button)findViewById(R.id.set9_1);
//        set9_2 = (Button)findViewById(R.id.set9_2);
//        set9_3 = (Button)findViewById(R.id.set9_3);
//        set9_4 = (Button)findViewById(R.id.set9_4);
//        set9_5 = (Button)findViewById(R.id.set9_5);
//        set9_6 = (Button)findViewById(R.id.set9_6);
//        set9_7 = (Button)findViewById(R.id.set9_7);
//        set9_8 = (Button)findViewById(R.id.set9_8);
//        set9_9 = (Button)findViewById(R.id.set9_9);
    }

    /**
     *  设置控件可见
     */
    protected void setViewVisibility() {
//        for(int i = 0; i < 9; i++){
//            for(int j = 0; j < 9; j++){
//                matrixButton[i][j].setVisibility(View.INVISIBLE);
//            }
//        }
    }
}
