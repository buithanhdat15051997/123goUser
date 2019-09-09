package sg.go.user.Interface;

import java.util.List;

import sg.go.user.Models.HospitalDischarge;
import sg.go.user.Models.Schedule;

public class DialogFragmentCallback {

    public interface ScheduleCallback{

        void onScheduleCallback(List<Schedule> scheduleList);

    }

    public interface HospitalDischargeCallback{

        void onHospitalDischargeCallback(HospitalDischarge hospitalDischarge);
        void onHospitalDischargeCancel(Boolean isDismiss);

    }

    public interface DialogDismissCallback{

        void onDialogDismiss (Boolean isDismiss);
    }
}
