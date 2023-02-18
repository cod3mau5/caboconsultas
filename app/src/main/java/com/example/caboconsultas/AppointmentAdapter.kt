package com.example.caboconsultas

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.caboconsultas.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(private val appointments: ArrayList<Appointment>)
    : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        fun bind(appointment: Appointment)=
            with(itemView){
                tvAppointmentId.text=context.getString(R.string.item_appointment_id,appointment.id)
                tvDoctorName.text=appointment.DoctorName
                tvScheduledDate.text=context.getString(R.string.item_appointment_date,appointment.scheduledDate)
                tvScheduledTime.text=context.getString(R.string.item_appointment_time,appointment.scheduledTime)
            }

    }
    // populate XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_appointment,parent,false)
        )
    }
    // Binds Data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment= appointments[position]
        holder.bind(appointment)
    }
    // return number of elements
    override fun getItemCount(): Int =  appointments.size

}