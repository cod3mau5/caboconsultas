package com.example.caboconsultas.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.caboconsultas.R
import com.example.caboconsultas.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter
    : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    var appointments= ArrayList<Appointment>()
    class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        fun bind(appointment: Appointment)=
            with(itemView){
                tvAppointmentId.text=context.getString(R.string.item_appointment_id,appointment.id)
                tvDoctorName.text=appointment.doctor.name
                tvScheduledDate.text=context.getString(R.string.item_appointment_date,appointment.scheduledDate)
                tvScheduledTime.text=context.getString(R.string.item_appointment_time,appointment.scheduledTime)
                tvSpecialty.text=appointment.specialty.name
                tvStatus.text=appointment.status
                tvType.text=appointment.type
                tvCreatedAt.text="Esta se registro el dia: ${appointment.CreatedAt} con la siguiente descripcion:"
                tvDescription.text=appointment.description

                ibExpand.setOnClickListener{
                    if(linearLayoutDetails.visibility==View.VISIBLE){
                        linearLayoutDetails.visibility=View.GONE
                        ibExpand.setImageResource(R.drawable.ic_baseline_expand_more_24)
                    }else{
                        ibExpand.setImageResource(R.drawable.ic_baseline_expand_less_24)
                        linearLayoutDetails.visibility=View.VISIBLE
                    }
                }
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