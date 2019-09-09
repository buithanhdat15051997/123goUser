package sg.go.user.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.Interface.DialogFragmentCallback;
import sg.go.user.R;

public class HospitalDischargeWarningDialog extends DialogFragment implements View.OnClickListener{

    @BindView(R.id.custom_simple_title)
    TextView dialog_title;

    @BindView(R.id.custom_simple_content)
    TextView dialog_content;

    @BindView(R.id.btn_yes)
    TextView btn_yes;

    @BindView(R.id.btn_no)
    TextView btn_no;

    private Activity mContext;
    private DialogFragmentCallback.DialogDismissCallback dialogDismissCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_simple_dialog);

        ButterKnife.bind(this, dialog);

        dialog_title.setText(getResources().getString(R.string.hospital_discharge_warning_title));
        dialog_content.setText(getResources().getString(R.string.hospital_discharge_warning_content));

        btn_yes.setText(getResources().getString(R.string.txt_ok));
        btn_yes.setOnClickListener(this);
        btn_no.setVisibility(View.GONE);

        return dialog;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_yes:{

                dismiss();

                if (dialogDismissCallback != null){

                    dialogDismissCallback.onDialogDismiss(true);

                }

            }
            break;
        }
    }

    public void setDialogDismissCallback(DialogFragmentCallback.DialogDismissCallback dialogDismissCallback) {
        this.dialogDismissCallback = dialogDismissCallback;
    }


}
