package com.example.mikhail.help.additions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mikhail.help.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataEventFragment extends DataFragment {

    private static final String TAG = "DataEventFragment";
    public static boolean isDatesOK = false;
    private FrameLayout selectBeginningDateLayout,
            selectBeginningTimeLayout,
            selectEndingDateLayout,
            selectEndingTimeLayout;
    private TextView selectBeginningDateText,
            selectBeginningTimeText,
            selectEndingDateText,
            selectEndingTimeText;
    private DataEventFragment.OnDataEventFragmentDataListener mListener;
    private Calendar startDate, endDate, startTime, endTime;
    private int MIN_EVENT_DURATION_IN_MINS = 15;

    private Calendar getCalendar(int year, int month, int day) {
        return new GregorianCalendar(year, month, day);
    }

    private Calendar getCalendar(int hour, int minute) {
        return new GregorianCalendar(0, 0, 0, hour, minute);
    }

    private void setTimeToDate(Calendar date, Calendar time) {
        int hour = time.get(Calendar.HOUR_OF_DAY), minute = time.get(Calendar.MINUTE);
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);
    }

    public boolean allDatesOK() {
        if (isDatesCorrect()) {
            if (startTime != null && endTime != null && startDate != null && endDate != null) {
                if ((endDate.getTimeInMillis() - startDate.getTimeInMillis()) / (1000 * 60) >= MIN_EVENT_DURATION_IN_MINS) {
                    if (startDate.after(Calendar.getInstance())) {
                        selectBeginningDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        selectBeginningTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        selectEndingDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        selectEndingTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        return true;
                    } else {
                        Toast.makeText(getContext(), R.string.events_cant_be_started_before_current_date, Toast.LENGTH_LONG).show();
                        selectBeginningDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                    }
                } else {
                    Toast.makeText(getContext(), String.format(getResources().getString(R.string.min_event_duration), MIN_EVENT_DURATION_IN_MINS), Toast.LENGTH_LONG).show();
                    selectEndingTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                }
            }
        }
        return false;
    }

    private boolean isDatesCorrect() {
        if (startDate == null) {
            return true;
        } else {
            if (endDate == null) {
                return true;
            } else {
                if (startTime == null || endTime == null) {
                    return startDate.before(endDate) || startDate.equals(endDate);
                } else {
                    setTimeToDate(startDate, startTime);
                    setTimeToDate(endDate, endTime);
                    return startDate.before(endDate) || startDate.equals(endDate);
                }
            }
        }
    }


    private String getDateLocalizedString(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
        return dateFormat.format(getCalendar(year, month, day).getTime());
    }

    private String getTimeLocalizedString(int hour, int minute) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm");
        return dateFormat.format(getCalendar(hour, minute).getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout chooseTimeLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.choose_time_event, null);
        ((LinearLayout) v.findViewById(R.id.container)).addView(chooseTimeLayout);

        selectBeginningDateLayout = chooseTimeLayout.findViewById(R.id.selectBeginningDateLayout);
        selectBeginningTimeLayout = chooseTimeLayout.findViewById(R.id.selectBeginningTimeLayout);
        selectEndingDateLayout = chooseTimeLayout.findViewById(R.id.selectEndingDateLayout);
        selectEndingTimeLayout = chooseTimeLayout.findViewById(R.id.selectEndingTimeLayout);

        selectBeginningDateText = chooseTimeLayout.findViewById(R.id.selectBeginningDateText);
        selectBeginningTimeText = chooseTimeLayout.findViewById(R.id.selectBeginningTimeText);
        selectEndingDateText = chooseTimeLayout.findViewById(R.id.selectEndingDateText);
        selectEndingTimeText = chooseTimeLayout.findViewById(R.id.selectEndingTimeText);

        selectBeginningDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c;

                if (startDate != null) c = startDate;
                else c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        R.style.Theme_AppCompat_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                startDate = getCalendar(year, monthOfYear, dayOfMonth);
                                selectBeginningDateText.setText(getDateLocalizedString(year, monthOfYear, dayOfMonth));

                                if (isDatesCorrect()) {
                                    mListener.OnDateStart(year, monthOfYear, dayOfMonth);
                                    selectBeginningDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                                } else {
                                    selectBeginningDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                                }

                                if (endDate == null) {

                                    endDate = getCalendar(year, monthOfYear, dayOfMonth);
                                    selectEndingDateText.setText(getDateLocalizedString(year, monthOfYear, dayOfMonth));

                                    if (isDatesCorrect()) {
                                        mListener.OnDateEnd(year, monthOfYear, dayOfMonth);
                                        selectEndingDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                                    } else {
                                        selectEndingDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                                    }
                                }
                                isDatesOK = allDatesOK();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        selectEndingDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c;

                if (endDate != null) c = endDate;
                else c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        R.style.Theme_AppCompat_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                endDate = getCalendar(year, monthOfYear, dayOfMonth);
                                selectEndingDateText.setText(getDateLocalizedString(year, monthOfYear, dayOfMonth));

                                if (isDatesCorrect()) {
                                    mListener.OnDateEnd(year, monthOfYear, dayOfMonth);
                                    selectEndingDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                                } else {
                                    selectEndingDateLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                                }
                                isDatesOK = allDatesOK();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        selectBeginningTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c;

                if (startTime != null) c = startTime;
                else c = Calendar.getInstance();

                final int hour = c.get(Calendar.HOUR_OF_DAY), minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime = getCalendar(hourOfDay, minute);
                        selectBeginningTimeText.setText(getTimeLocalizedString(hourOfDay, minute));

                        if (isDatesCorrect()) {
                            mListener.OnTimeStart(hourOfDay, minute);
                            selectBeginningTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        } else {
                            selectBeginningTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                        }

                        if (endTime == null) {

                            endTime = getCalendar(hourOfDay, minute);

                            selectEndingTimeText.setText(getTimeLocalizedString(hourOfDay, minute));

                            if (isDatesCorrect()) {
                                mListener.OnTimeEnd(hourOfDay, minute);
                                selectEndingTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                            } else {
                                selectEndingDateText.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                            }
                        }
                        isDatesOK = allDatesOK();
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        selectEndingTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c;

                if (endTime != null) c = endTime;
                else c = Calendar.getInstance();

                final int hour = c.get(Calendar.HOUR_OF_DAY), minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime = getCalendar(hourOfDay, minute);
                        selectEndingTimeText.setText(getTimeLocalizedString(hourOfDay, minute));

                        if (isDatesCorrect()) {
                            mListener.OnTimeEnd(hourOfDay, minute);
                            selectEndingTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border_selected, null));
                        } else {
                            selectEndingTimeLayout.setBackground(getResources().getDrawable(R.drawable.button_border, null));
                        }
                        isDatesOK = allDatesOK();
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DataEventFragment.OnDataEventFragmentDataListener) {
            mListener = (DataEventFragment.OnDataEventFragmentDataListener) context;
        }
    }

    public interface OnDataEventFragmentDataListener {
        void OnDateStart(int year, int month, int day);

        void OnDateEnd(int year, int month, int day);

        void OnTimeStart(int hours, int minutes);

        void OnTimeEnd(int hours, int minutes);
    }
}
