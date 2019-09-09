package sg.go.user.Dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.Interface.DialogFragmentCallback;
import sg.go.user.Models.HospitalDischarge;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;

public class HospitalDischargeOptionDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.img_confirm)
    ImageView img_confirm;

    @BindView(R.id.edt_patient_name_value)
    EditText edt_patient_name_value;

    @BindView(R.id.edt_ward_value)
    EditText edt_ward_value;

    @BindView(R.id.edt_hospital_value)
    EditText edt_hospital_value;

    @BindView(R.id.edt_room_value)
    EditText edt_room_value;

    @BindView(R.id.edt_bed_value)
    EditText edt_bed_value;

    @BindView(R.id.tv_time_of_discharge_value)
    TextView tv_time_of_discharge_value;

    private Activity mActivity;
    private String dateTime = "", timeSet = "", date = "", time = "", pickupDateTime = "";
    private HospitalDischarge mHospitalDischarge;
    private DialogFragmentCallback.HospitalDischargeCallback hospitalDischargeCallback;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        if (getArguments() != null){

            mHospitalDischarge = getArguments().getParcelable(Const.HOSPITAL_DISCHARGE_OPTION);

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(mActivity, R.style.DialogSlideAnim_leftright_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_hosiptal_discharge_option);

        ButterKnife.bind(this, dialog);

        if (mHospitalDischarge != null){

            edt_patient_name_value.setText(mHospitalDischarge.getPatientName());
            edt_ward_value.setText(mHospitalDischarge.getWardNumber());
            edt_hospital_value.setText(mHospitalDischarge.getHospital());
            edt_room_value.setText(mHospitalDischarge.getRoomNumber());
            edt_bed_value.setText(mHospitalDischarge.getBedNumber());
            tv_time_of_discharge_value.setText(mHospitalDischarge.getTimeOfDischarge());

        }

        img_close.setOnClickListener(this);
        img_confirm.setOnClickListener(this);
        tv_time_of_discharge_value.setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.img_close:{

                if (hospitalDischargeCallback != null){

                    hospitalDischargeCallback.onHospitalDischargeCancel(true);

                }
                dismiss();
            }
            break;

            case R.id.img_confirm:{

                mHospitalDischarge = getHospitalDischarge();

                if (hospitalDischargeCallback != null && mHospitalDischarge != null){

                    hospitalDischargeCallback.onHospitalDischargeCallback(mHospitalDischarge);
                    dismiss();

                }

            }
            break;

            case R.id.tv_time_of_discharge_value:{

                DatePicker();

            }
            break;
        }
    }

    private HospitalDischarge getHospitalDischarge(){

        HospitalDischarge hospitalDischarge = null;

        if (edt_patient_name_value.getText().toString().length() == 0){

            edt_patient_name_value.setError(getResources().getString(R.string.edittext_empty_notice));

        }else if (edt_ward_value.getText().toString().length() == 0){

            edt_ward_value.setError(getResources().getString(R.string.edittext_empty_notice));

        }else if (edt_room_value.getText().toString().length() == 0){

            edt_room_value.setError(getResources().getString(R.string.edittext_empty_notice));

        }else if (edt_hospital_value.getText().toString().length() == 0){

            edt_hospital_value.setError(getResources().getString(R.string.edittext_empty_notice));

        }else if (edt_bed_value.getText().toString().length() == 0){

            edt_bed_value.setError(getResources().getString(R.string.edittext_empty_notice));

        }else if (tv_time_of_discharge_value.length() == 0){

            EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error_date_time), mActivity);

        }else {

            hospitalDischarge = new HospitalDischarge();
            hospitalDischarge.setPatientName(edt_patient_name_value.getText().toString().trim());
            hospitalDischarge.setWardNumber(edt_ward_value.getText().toString().trim());
            hospitalDischarge.setHospital(edt_hospital_value.getText().toString().trim());
            hospitalDischarge.setRoomNumber(edt_room_value.getText().toString().trim());
            hospitalDischarge.setBedNumber(edt_bed_value.getText().toString().trim());
            hospitalDischarge.setTimeOfDischarge(tv_time_of_discharge_value.getText().toString());
        }

        return hospitalDischarge;
    }

    public DialogFragmentCallback.HospitalDischargeCallback getHospitalDischargeCallback() {
        return hospitalDischargeCallback;
    }

    public void setHospitalDischargeCallback(DialogFragmentCallback.HospitalDischargeCallback hospitalDischargeCallback) {
        this.hospitalDischargeCallback = hospitalDischargeCallback;
    }

    private void DatePicker() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        dpd = new DatePickerDialog(getActivity(), R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(android.widget.DatePicker view,
                                          int year, int monthOfYear, int dayOfMonth) {
                        // txtDate.setText(dayOfMonth + "-"
                        // + (monthOfYear + 1) + "-" + year);

                        if (view.isShown()) {
                            // Toast.makeText(
                            // getActivity(),
                            // dayOfMonth + "-" + (monthOfYear + 1) + "-"
                            // + year, Toast.LENGTH_LONG).show();

                            date = Integer.toString(year) + "-"
                                    + Integer.toString(monthOfYear + 1) + "-"
                                    + Integer.toString(dayOfMonth);

                            dateTime = date;

                            TimePicker();

                            dpd.dismiss();
                        }
                    }
                }, mYear, mMonth, mDay);

        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dpd.dismiss();
                    }
                });
        // dpd.getDatePicker().setMaxDate(addDays(new Date(),90).getTime());
        // dpd.getDatePicker().setMinDate(new Date().getTime());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        /*cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));*/
        cal.set(Calendar.SECOND, 0);
        /*cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));*/
        cal.set(Calendar.MILLISECOND, 0);
        dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());

        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        /*cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));*/
        cal.set(Calendar.SECOND, 0);
        /*cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));*/
        cal.set(Calendar.MILLISECOND, 0);
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());


        //Cancel already scheduled reminders

        dpd.show();
    }

    public void TimePicker() {

        //Log.d("pavan", "in time picker");

        final Calendar calendar = Calendar.getInstance();


        calendar.add(Calendar.MINUTE, 30);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);


        tpd = new TimePickerDialog(getActivity(), R.style.datepicker,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(android.widget.TimePicker view,
                                          int hourOfDay, int minute) {
                        // txtTime.setText(hourOfDay + ":" + minute);
                        if (view.isShown()) {
                            tpd.dismiss();
                            // isTimePickerOpen = false;
                            // call api here
                            int hour = hourOfDay;
                            int min = minute;

                            if (hourOfDay > 12){

                                hour -= 12;
                                timeSet = "PM";
                            }else if (hourOfDay == 0){

                                timeSet = "AM";

                            }else if (hourOfDay == 12){

                                timeSet = "PM";
                            }else {

                                timeSet = "AM";

                            }

                            if (minute < 10){

                                time = String.valueOf(hourOfDay)+ ":0" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            }else {

                                time = String.valueOf(hourOfDay)+ ":" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            }

                            pickupDateTime = date + " " + time;

                            dateTime = dateTime.concat(" "
                                    + Integer.toString(hourOfDay) + ":"
                                    + Integer.toString(minute) + ":" + "00");

                            tv_time_of_discharge_value.setText(dateTime);

                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            /*AlarmUtil.scheduleNotification(getActivity(), calendar);*/
                        }
                    }
                }, mHour, mMinute, false);

        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tpd.dismiss();
                    }
                });



        tpd.show();

    }
}
