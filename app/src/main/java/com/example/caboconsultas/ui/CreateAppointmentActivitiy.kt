package com.example.caboconsultas.ui

import android.app.DatePickerDialog
import android.os.Bundle
//import android.support.design.widget.Snackbar
//import android.support.v7.app.AlertDialog
//import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.io.response.SimpleResponse
import com.example.caboconsultas.model.Doctor
import com.example.caboconsultas.model.Schedule
import com.example.caboconsultas.model.Specialty
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment_activitiy.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CreateAppointmentActivitiy : AppCompatActivity() {
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }
    private val apiService: ApiService by lazy {
        ApiService.create()
    }
    private var selectedCalendar= Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment_activitiy)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 4)
                etDescription.error=getString(R.string.validar_descripcion_en_cita)
            else{
//                continue to step 2

                cvStep1.visibility=View.GONE
                cvStep2.visibility=View.VISIBLE
            }
        }
        btnNext2.setOnClickListener {
//                continue to step 3
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error=getString(R.string.validar_fecha_de_cita)
                }
                selectedTimeRadioButton == null -> {
                    Snackbar.make(createAppointmentLinearLayout,
                        R.string.validar_hora_de_cita, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    cvStep2.visibility=View.GONE
                    cvStep3.visibility=View.VISIBLE
                }
            }
        }
        btnConfirmAppointment.setOnClickListener {
            it.isClickable=false
            performStoreAppointment()
            it.isClickable=true
        }

        loadSpecialties()
        listenSpecialtyChanges()
        listenDoctorAndDateChanges()

    }

    private fun performStoreAppointment() {
        val token= preferences["token",""]
        val authHeader="Bearer $token"
        val description= tvConfirmDescription.text.toString()
        val specialty= spinnerSpecialty.selectedItem as Specialty
        val doctor= spinnerDoctors.selectedItem as Doctor
        val scheduledDate= tvConfirmDate.text.toString()
        val scheduledTime= tvConfirmTime.text.toString()
        val type= tvConfirmType.text.toString()

        val call= apiService.storeAppointment(
            authHeader, description,
            specialty.id, doctor.id,
            scheduledDate, scheduledTime,
            type
        )
        call.enqueue(object: Callback<SimpleResponse>{
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful){
                    toast("Cita registrada correctamente")
                    finish()
                }else{
                    toast("Ocurrio un errror inesperado resgistrando la cita medica.")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })

    }

    private fun listenDoctorAndDateChanges() {
        // doctor spinner changes
        spinnerDoctors.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val doctor= adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // date spinner changes
        etScheduledDate.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val doctor:Doctor=spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id ,etScheduledDate.text.toString())
            }
        })
    }

    private fun loadHours(id: Int, date: String) {
        if (date.isEmpty()){
            return
        }
        val call=apiService.getHours(id,date)
        call.enqueue(object: Callback<Schedule>{
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if(response.isSuccessful){
                    val schedule= response.body()!!

                    if (schedule.morning.size==0 && schedule.afternoon.size==0){
                        Toast.makeText(this@CreateAppointmentActivitiy,"El medico no trabaja ese dia!!",Toast.LENGTH_SHORT).show()
                    }
//                    val hours= arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
                    schedule?.let {
                        tvSelectDoctorAnDate.visibility=View.GONE
                        val intervals= it.morning + it.afternoon
                        val hours= ArrayList<String>()

                        intervals.forEach { interval->
                            hours.add(interval.start)
                        }
                        displayIntervalRadios(hours)
                    }
                }
            }
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivitiy,getString(R.string.no_se_pudo_cargar_horas),Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun listenSpecialtyChanges() {
        spinnerSpecialty.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val specialty= adapter?.getItemAtPosition(position) as Specialty
                loadDoctors(specialty.id)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun loadDoctors(specialtyId: Int) {
        val call= apiService.getDoctors(specialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivitiy, getString(R.string.error_cargando_doctores), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>) {
                if (response.isSuccessful){ // (200 ... 300)
                    val doctors= response.body()!!
                    spinnerDoctors  .adapter=
                        ArrayAdapter<Doctor>(this@CreateAppointmentActivitiy,android.R.layout.simple_list_item_1,doctors)
                }
            }
        })
    }

    private fun loadSpecialties() {
        val call= apiService.getSpecialties()
        call.enqueue(object: Callback<ArrayList<Specialty>>{
            override fun onResponse(
                call: Call<ArrayList<Specialty>>,
                response: Response<ArrayList<Specialty>>
            ) {
                if (response.isSuccessful){ // (200 ... 300)
                    val specialties= response.body()!!
                    spinnerSpecialty.adapter=
                        ArrayAdapter<Specialty>(this@CreateAppointmentActivitiy,android.R.layout.simple_list_item_1,specialties)
                }
            }
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivitiy,getString(R.string.error_cargando_especialidades),Toast.LENGTH_SHORT).show()
                finish()
            }

        })


    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text=etDescription.text.toString()
        tvConfirmSpecialty.text=spinnerSpecialty.selectedItem.toString()

        val selectedRadioBtnId=rgType.checkedRadioButtonId
        val selectedRadioType=rgType.findViewById<RadioButton>(selectedRadioBtnId)
        tvConfirmType.text=selectedRadioType.text.toString()

        tvConfirmDoctorName.text=spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text=etScheduledDate.text.toString()
        tvConfirmTime.text=selectedTimeRadioButton?.text.toString()
    }

    fun onClickScheduledDate(view: View) {
        val year= selectedCalendar.get(Calendar.YEAR)
        val month= selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth=selectedCalendar.get(Calendar.DAY_OF_MONTH)
        val listener= DatePickerDialog.OnDateSetListener{ datePicker, y, m, d ->
            Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)
            var m = (m+1).toDigits()
            var d = d.toDigits()
            etScheduledDate.setText(resources.getString(R.string.date_format,y,m,d))
            etScheduledDate.error=null
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
    private fun displayIntervalRadios(hours: ArrayList<String>){

        selectedTimeRadioButton=null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()
        if (hours.isEmpty()){
            tvSelectDoctorAnDate.visibility= View.GONE
            notAvailableHours.visibility= View.VISIBLE
            return
        }

        notAvailableHours.visibility= View.GONE
        var goToLeft=true

        hours.forEach {
            val radioButton= RadioButton(this)
            radioButton.id=View.generateViewId()
            radioButton.text=it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioButton?.isChecked=false
                selectedTimeRadioButton= view as RadioButton?
                selectedTimeRadioButton?.isChecked=true
            }
            if(goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft=!goToLeft
        }
    }
    private fun Int.toDigits() = if(this>9) this.toString() else "0$this"
    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility=View.GONE
                cvStep2.visibility=View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility=View.GONE
                cvStep1.visibility=View.VISIBLE
            }
            cvStep1.visibility==View.VISIBLE -> {
                val builder= AlertDialog.Builder(this)
                builder.setTitle("Estás seguro que deseas salir?")
                builder.setMessage("Si abandonas el registro, los datos que habias ingresado se perderan.")
                builder.setPositiveButton("Si, salir"){ _, _ ->
                    finish()
                }
                builder.setNegativeButton("Continuar registro"){dialog,_->dialog.dismiss()}
                val dialog=builder.create()
                dialog.show()
            }
        }
    }
}