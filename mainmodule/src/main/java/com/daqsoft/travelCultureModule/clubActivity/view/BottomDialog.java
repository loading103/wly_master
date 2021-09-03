package com.daqsoft.travelCultureModule.clubActivity.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.daqsoft.mainmodule.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomDialog extends BottomSheetDialog {

    private BottomSheetBehavior behavior;
    private CallBack callBack;
    public  EditText et;
    public BottomDialog(@NonNull Context context, boolean isTranslucentStatus,CallBack callBack) {
        super(context);
        Window window = getWindow();
        this.callBack=callBack;
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            if (isTranslucentStatus) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            window.setBackgroundDrawableResource(R.color.color_alpha_50);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initialize(view);
        et=(EditText) view.findViewById(R.id.et_content_say);
        view.findViewById(R.id.tv_content_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.postdata(et.getText().toString());
                dismiss();
            }
        });
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (behavior != null) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        dismiss();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void initialize(final View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        behavior = (BottomSheetBehavior) params.getBehavior();
        if (behavior == null) {
            return;
        }
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
 public interface   CallBack{
        void postdata(String content);
    }
}
