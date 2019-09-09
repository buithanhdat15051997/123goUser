package sg.go.user.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import sg.go.user.Interface.Adapter;
import sg.go.user.Models.Schedule;
import sg.go.user.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NurseBookingScheduleAdapter extends RecyclerView.Adapter<NurseBookingScheduleAdapter.NurseScheduleViewHolder> {

    private Activity mActivity;
    private List<Schedule> mScheduleList;
    private Adapter.AdaperCallbackListCalendar adaperCallbackListCalendar;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat inputFormat;
    private View mView;

    public NurseBookingScheduleAdapter(Activity mActivity, List<Schedule> scheduleList) {
        this.mActivity = mActivity;
        this.mScheduleList = scheduleList;
        this.simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void setAdaperCallbackListCalendar(Adapter.AdaperCallbackListCalendar adaperCallbackListCalendar) {
        this.adaperCallbackListCalendar = adaperCallbackListCalendar;
    }

    @NonNull
    @Override
    public NurseScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mView = LayoutInflater.from(mActivity).inflate(R.layout.schedule_item, null);

        NurseScheduleViewHolder nurseScheduleViewHolder = new NurseScheduleViewHolder(mView);

        return nurseScheduleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NurseScheduleViewHolder nurseScheduleViewHolder, final int i) {

        final Schedule schedule = mScheduleList.get(i);

        if (schedule != null){

            String schedule_date = "";
            schedule_date = schedule.getRequest_date();

            try {

                Date date = inputFormat.parse(schedule_date);
                schedule_date = simpleDateFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            nurseScheduleViewHolder.tv_schedule_service_type.setText(schedule.getRequest_type());

            nurseScheduleViewHolder.tv_schedule_date.setText(schedule_date);

            Glide.with(mActivity)
                    .load(schedule.getRequest_pic())
                    .apply(new RequestOptions().error(R.drawable.ambulance_car))
                    .into(nurseScheduleViewHolder.schedule_car);

            nurseScheduleViewHolder.tv_schedule_source.setText(schedule.getS_address());
            nurseScheduleViewHolder.tv_schedule_source.setSelected(true);

            nurseScheduleViewHolder.tv_schedule_destination.setSelected(true);
            if (!schedule.getD_address().equals("")){

                nurseScheduleViewHolder.tv_schedule_destination.setText(schedule.getD_address());

            }else {

                nurseScheduleViewHolder.tv_schedule_destination.setText(mActivity.getResources().getString(R.string.not_available));

            }

            nurseScheduleViewHolder.tv_schedule_status.setVisibility(View.GONE);

            nurseScheduleViewHolder.cancel_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mScheduleList.remove(i);

                    notifyDataSetChanged();

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }


    public class NurseScheduleViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_schedule_service_type, tv_schedule_date, tv_schedule_source, tv_schedule_destination, tv_schedule_status;
        private ImageButton cancel_schedule;
        private CircleImageView schedule_car;

        public NurseScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            schedule_car = (CircleImageView) itemView.findViewById(R.id.schedule_car);
            tv_schedule_service_type = (TextView) itemView.findViewById(R.id.tv_schedule_service_type);
            tv_schedule_date = (TextView) itemView.findViewById(R.id.tv_schedule_date);
            cancel_schedule = (ImageButton) itemView.findViewById(R.id.cancel_schedule);
            tv_schedule_source = (TextView)itemView.findViewById(R.id.tv_schedule_source);
            tv_schedule_destination = (TextView)itemView.findViewById(R.id.tv_schedule_destination);
            tv_schedule_status = (TextView)itemView.findViewById(R.id.tv_schedule_status);
        }

    }
}
