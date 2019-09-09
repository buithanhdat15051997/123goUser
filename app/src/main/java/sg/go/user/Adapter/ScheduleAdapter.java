package sg.go.user.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.go.user.Interface.DialogFragmentCallback;
import sg.go.user.Models.Schedule;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Dialog.CancellationPolicyDialog;

/**
 * Created by user on 2/3/2017.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.typesViewHolder>{

    private Activity mContext;
    private List<Schedule> mScheduleList;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat inputformat;

    public ScheduleAdapter(Activity context, List<Schedule> mScheduleList) {
        mContext = context;
        simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
        inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      /*  simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        inputformat.setTimeZone(TimeZone.getTimeZone("GMT"));*/

        this.mScheduleList = mScheduleList;

    }

    @Override
    public ScheduleAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_item, null);
        ScheduleAdapter.typesViewHolder holder = new ScheduleAdapter.typesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ScheduleAdapter.typesViewHolder holder, int position) {
        final Schedule schedule = mScheduleList.get(position);

        if (schedule != null) {
            String later_Date = "";
            try {

                later_Date = schedule.getRequest_date();
                Date date = inputformat.parse(later_Date);
                later_Date = simpleDateFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_later_service_type.setText(schedule.getRequest_type());

            holder.tv_later_date.setText(later_Date);

            Glide.with(mContext)
                    .load(schedule.getRequest_pic())
                    .apply(new RequestOptions().error(R.drawable.ambulance_car))
                    .into(holder.later_car);

            holder.tv_later_source.setText(schedule.getS_address());
            holder.tv_later_source.setSelected(true);

            holder.tv_later_destination.setSelected(true);
            if(!schedule.getD_address().equals("")){
                holder.tv_later_destination.setText(schedule.getD_address());

            } else {
                holder.tv_later_destination.setText(mContext.getResources().getString(R.string.not_available));
            }

            if (new PreferenceHelper(mContext).getLoginType().equals(Const.PatientService.PATIENT)){ //Check Login type == Consumer

                holder.cancel_later.setVisibility(View.VISIBLE);

                holder.cancel_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Const.SCHEDULE, schedule);
                        CancellationPolicyDialog cancellationPolicyDialog = new CancellationPolicyDialog();
                        cancellationPolicyDialog.setArguments(bundle);
                        cancellationPolicyDialog.show(manager, Const.CANCELLATION_POLICY_DIALOGFRAGMENT);
                        cancellationPolicyDialog.getScheduleList(new DialogFragmentCallback.ScheduleCallback() {
                            @Override
                            public void onScheduleCallback(List<Schedule> scheduleList) {

                                if (scheduleList != null){

                                    EbizworldUtils.appLogDebug("HaoLS", "DialogFragmentCallback.ScheduleCallback(): " + scheduleList.size());
                                    mScheduleList.clear();
                                    mScheduleList = scheduleList;
                                    notifyDataSetChanged();
                                }
                            }
                        });

                    }
                });

            }else if (new PreferenceHelper(mContext).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){ //Check Login type == Nurse

                holder.cancel_later.setVisibility(View.VISIBLE);

                holder.cancel_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Const.SCHEDULE, schedule);
                        CancellationPolicyDialog cancellationPolicyDialog = new CancellationPolicyDialog();
                        cancellationPolicyDialog.setArguments(bundle);
                        cancellationPolicyDialog.show(manager, Const.CANCELLATION_POLICY_DIALOGFRAGMENT);
                        cancellationPolicyDialog.getScheduleList(new DialogFragmentCallback.ScheduleCallback() {
                            @Override
                            public void onScheduleCallback(List<Schedule> scheduleList) {

                                if (scheduleList != null){

                                    EbizworldUtils.appLogDebug("HaoLS", "DialogFragmentCallback.ScheduleCallback(): " + scheduleList.size());
                                    mScheduleList.clear();
                                    mScheduleList = scheduleList;
                                    notifyDataSetChanged();
                                }
                            }
                        });

                    }
                });

            }else if (new PreferenceHelper(mContext).getLoginType().equals(Const.HospitalService.HOSPITAL)){ //Check Login type == Hospital

                holder.cancel_later.setVisibility(View.VISIBLE);
                holder.cancel_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Const.SCHEDULE, schedule);
                        final CancellationPolicyDialog cancellationPolicyDialog = new CancellationPolicyDialog();
                        cancellationPolicyDialog.setArguments(bundle);
                        cancellationPolicyDialog.show(manager, Const.CANCELLATION_POLICY_DIALOGFRAGMENT);
                        cancellationPolicyDialog.getScheduleList(new DialogFragmentCallback.ScheduleCallback() {
                            @Override
                            public void onScheduleCallback(List<Schedule> scheduleList) {

                                if (scheduleList != null){

                                    EbizworldUtils.appLogDebug("HaoLS", "DialogFragmentCallback.ScheduleCallback(): " + scheduleList.size());
                                    mScheduleList.clear();
                                    mScheduleList = scheduleList;
                                    notifyDataSetChanged();
                                }
                            }
                        });

                    }
                });
            }

//            Check request status
            if (!schedule.getStatus_request().equals("")){

                holder.status_schedule.setText(schedule.getStatus_request());

                if (schedule.getStatus_request().equals(Const.ScheduleStatus.APPROVED)){

                    holder.status_schedule.setText(mContext.getResources().getString(R.string.accepted));

                }else if (schedule.getStatus_request().equals(Const.ScheduleStatus.WAITING_START)){

                        holder.status_schedule.setText(mContext.getResources().getString(R.string.waiting_start));

                }

            }

        }

    }

    @Override
    public int getItemCount() {

        return mScheduleList.size();

    }

    public class typesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_later_service_type, tv_later_date,tv_later_source,tv_later_destination, status_schedule;
        private ImageButton cancel_later;
        private CircleImageView later_car;

        public typesViewHolder(View itemView) {
            super(itemView);
            later_car = (CircleImageView) itemView.findViewById(R.id.schedule_car);
            tv_later_service_type = (TextView) itemView.findViewById(R.id.tv_schedule_service_type);
            tv_later_date = (TextView) itemView.findViewById(R.id.tv_schedule_date);
            cancel_later = (ImageButton) itemView.findViewById(R.id.cancel_schedule);
            tv_later_source = (TextView)itemView.findViewById(R.id.tv_schedule_source);
            tv_later_destination = (TextView)itemView.findViewById(R.id.tv_schedule_destination);
            status_schedule = (TextView) itemView.findViewById(R.id.tv_schedule_status);
        }
    }


}


