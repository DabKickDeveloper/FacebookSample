package com.dabkick.partner.fb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.dabkick.sdk.Global.CustomDialog;


/**
 * Created by developer3 on 4/10/15.
 */
public class DialogHelper {

    final static AlertDialog[] currentDialog = new AlertDialog[1];

    public static class Title{

    }

    public static class Message{
        final static public String NETWORK_DISCONNECTED = "Your network is disconnected. Check your wifi or mobile connection.";
        final static public String VIDEO_URL_NOT_AVAILIBALE = "We can not play the video at this time.";
        final static public String MUSIC_URL_NOT_AVAILIBALE = "We can not play the music at this time.";
        final static public String IMAGE_NOT_AVAILABLE = "This picture is not available.";
    }

    public static CustomDialog popupAlertDialog(Context c,String title, String message, String yesText, final Runnable yes,String noText,final Runnable no)
    {
        return popupAlertDialog(c,title,message,true,yesText,yes,noText,no);
    }

    public static CustomDialog popupAlertDialog(Context c,String title, String message, boolean cancelable,String yesText, final Runnable yes,String noText,final Runnable no)
    {
//        if (yesText != null)
//            yesText = yesText.toLowerCase();
//
//        if (noText != null)
//            noText = noText.toLowerCase();

        //Deepak added
        //30Nov15
        //Material design alert dialog box
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);

        builder.setCancelable(cancelable);

        if(title == null) {
            builder.setMessage(message);
        }
        else {
            builder.setTitle(title);
            builder.setMessage(message);
        }

        if(noText != null) {
            builder.setPositiveButton(yesText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (yes != null) {
                        Thread thread = new Thread(yes);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            builder.setNegativeButton(noText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Thread thread = new Thread(no);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            builder.setPositiveButton(yesText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (yes != null) {
                        Thread thread = new Thread(yes);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


        currentDialog[0] = builder.show();
        CustomDialog customDialog = new CustomDialog(currentDialog[0]);

        return customDialog;

//        final Dialog alertDialog = new Dialog(c);
//        alertDialog.setContentView(R.layout.default_alert_dialog);
//        alertDialog.setCancelable(cancelable);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //vallabh added to dismiss the keyboard
//        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        RelativeLayout doubleButtonRelativeLayout = (RelativeLayout)alertDialog.findViewById(R.id.double_button_relative_layout);
//        RelativeLayout singleButtonRelativeLayout = (RelativeLayout)alertDialog.findViewById(R.id.single_button_relative_layout);
//        TextView titleTxt = (TextView)alertDialog.findViewById(R.id.title_txt);
//        TextView msgTxt = (TextView)alertDialog.findViewById(R.id.message);
//        TextView positiveBtnText = (TextView)alertDialog.findViewById(R.id.yes);
//        TextView negativeBtnText = (TextView)alertDialog.findViewById(R.id.no);
//        final RelativeLayout positiveButton = (RelativeLayout)alertDialog.findViewById(R.id.layout_yes);
//        final RelativeLayout negativeButton = (RelativeLayout)alertDialog.findViewById(R.id.layout_no);
//
//        TextView SingleButtonText = (TextView)alertDialog.findViewById(R.id.single_button_txt);
//        final RelativeLayout layoutSingleButton = (RelativeLayout)alertDialog.findViewById(R.id.layout_single_button);
//
//        titleTxt.setText(title);
//        msgTxt.setText(message);
//        if(title == null)
//            titleTxt.setVisibility(View.GONE);
//        else
//            titleTxt.setVisibility(View.VISIBLE);
//
//        if(noText != null) {
//            doubleButtonRelativeLayout.setVisibility(View.VISIBLE);
//            singleButtonRelativeLayout.setVisibility(View.GONE);
//            positiveBtnText.setText(yesText);
//            negativeBtnText.setText(noText);
//
//            //check the noText, if "no" make it red else make it grey
//            if(noText.equals("no"))
//                negativeBtnText.setTextColor(Color.RED);
//            else
//                negativeBtnText.setTextColor(Color.GRAY);
//
//            if(BaseActivity.mCurrentActivity instanceof LiveChat && message.equals(DialogHelper.Message.LIVE_SESSION_ENDED) ){
//                positiveBtnText.setTextColor(Color.GRAY);
//            }else if(BaseActivity.mCurrentActivity instanceof NotificationActivity || BaseActivity.mCurrentActivity instanceof RateThisAppActivity){
//                negativeBtnText.setTextColor(Color.GRAY);
//            }
//            positiveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    GlobalHandler.alphaFlashAnimation(positiveButton, new Runnable() {
//                        @Override
//                        public void run() {
//                            if (yes != null) {
//                                CustomDialog customDialog = DialogManager.getInstance().getCustomDialog(alertDialog);
//                                if (customDialog != null && (customDialog.getPushType().equals(GCMIntentService.PushType.FRIENDED) || customDialog.getPushType().equals(GCMIntentService.PushType.DABMAIL)))
//                                    DialogManager.getInstance().dismissRemoveDialogWithPushType(customDialog.getPushType());
//
//                                DialogManager.getInstance().removeDialog(alertDialog,true);
//                                Thread thread = new Thread(yes);
//                                thread.start();
//                                try {
//                                    thread.join();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            GlobalHandler.runOnUIThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    alertDialog.dismiss();
//                                }
//                            });
//                        }
//                    });
//
//
//                }
//            });

//            negativeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    GlobalHandler.alphaFlashAnimation(negativeButton, new Runnable() {
//                        @Override
//                        public void run() {
//                            DialogManager.getInstance().removeDialog(alertDialog, true);
//                            Thread thread = new Thread(no);
//                            thread.start();
//                            try {
//                                thread.join();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                            GlobalHandler.runOnUIThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    alertDialog.dismiss();
//                                }
//                            });
//                        }
//                    });
//
//                }
//            });
//
//
//        }else{
//            doubleButtonRelativeLayout.setVisibility(View.GONE);
//            singleButtonRelativeLayout.setVisibility(View.VISIBLE);
//            SingleButtonText.setText(yesText);
//
//            layoutSingleButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (yes != null) {
//                        DialogManager.getInstance().removeDialog(alertDialog,true);
//
//                        Thread thread = new Thread(yes);
//                        thread.start();
//                        try {
//                            thread.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    GlobalHandler.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            alertDialog.dismiss();
//                        }
//                    });
//                }
//            });
//        }
//
//        //vallabh added
//        // to enable the dismissing of the alert box when user presses the back button of the device
//
//        final String finalNoText = noText;
//        final String finalYesText = yesText;
//        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                if(finalNoText !=null){
//                    negativeButton.callOnClick();
//                }else{
//                    if(finalYesText.equals("update")){
//                        dialogInterface.dismiss();
//
//                    }else
//                        layoutSingleButton.callOnClick();
//                }
//            }
//        });
//
//        GlobalHandler.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                alertDialog.show();
//            }
//        });
//
//        CustomDialog customDialog = new CustomDialog(alertDialog);
//        DialogManager.getInstance().addDialog(customDialog);
//
//        return customDialog;

    }

    public static CustomDialog popupAlertDialog(Context c,String title, String message,String yesText)
    {
        return popupAlertDialog(c, title, message, yesText, null, null, null);
    }

    public static CustomDialog popupAlertDialog(Context c,String title, String message,String yesText,Runnable finishRunnable)
    {
        return popupAlertDialog(c,title,message,yesText,finishRunnable,null,null);
    }

    //Deepak added
    //16Jun15
    //Thanks Vallabh
    //checking weather the intent (market) is present or not
    public static boolean MyStartActivity(Activity mActivity, Intent aIntent) {
        try {
            mActivity.startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

}
