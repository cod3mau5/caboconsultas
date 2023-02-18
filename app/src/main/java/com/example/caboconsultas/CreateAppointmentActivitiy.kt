package com.example.caboconsultas

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_appointment_activitiy.*
import java.util.*

class CreateAppointmentActivitiy : AppCompatActivity() {
    private var selectedCalendar= Calendar.getInstance()
    private var selectedRadioButton: RadioButton? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment_activitiy)
        btnNext.setOnClickListener {
            cvStep1.visibility=View.GONE
            cvStep2.visibility=View.VISIBLE
        }
        btnConfirmAppointment.setOnClickListener {
            finish()
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_SHORT).show()
        }
        val specialtyOptions= arrayOf("Specialty A","Specialty B","Specialty C")
        spinnerSpecialty.adapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,specialtyOptions)

        val doctorOptions= arrayOf("Doctor A","Doctor B","Doctor C")
        spinnerDoctors.adapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,doctorOptions)

    }

    fun onClickScheduledDate(view: View) {
        val year= selectedCalendar.get(Calendar.YEAR)
        val month= selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth=selectedCalendar.get(Calendar.DAY_OF_MONTH)
        val listener= DatePickerDialog.OnDateSetListener{ datePicker, y, m, d ->
            Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)
            var m = m.toDigits()
            var d = d.toDigits()
            etScheduledDate.setText(resources.getString(R.string.date_format,y,m,d))
            displayRadioButtons()
        }
        // new dialog
        val datepickerDialog= DatePickerDialog(this,listener,year,month,dayOfMonth)
        val datePicker=datepickerDialog.datePicker

        // set limits
        val calendar=Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,1)
        datePicker.minDate= calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH,29)
        datePicker.maxDate=calendar.timeInMillis

        // show dialog
        datepickerDialog.show()
    }
    private fun displayRadioButtons(){
//        radioGroup.clearCheck()
        selectedRadioButton=null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()
        val hours= arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft=true


        hours.forEach {
            val radioButton= RadioButton(this)
            radioButton.id=View.generateViewId()
            radioButton.text=it

            radioButton.setOnClickListener { view ->
                selectedRadioButton?.isChecked=false
                selectedRadioButton= view as RadioButton?
                selectedRadioButton?.isChecked=true
            }
            if(goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft=!goToLeft
        }
    }
    private fun Int.toDigits() = if(this>9) this.toString() else "0$this"
}