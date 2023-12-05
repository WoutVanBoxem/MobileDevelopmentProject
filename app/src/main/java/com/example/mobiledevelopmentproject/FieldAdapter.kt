package com.example.mobiledevelopmentproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FieldAdapter(private val fields: List<Field>) : RecyclerView.Adapter<FieldAdapter.FieldViewHolder>() {

    class FieldViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFieldName: TextView = view.findViewById(R.id.tvFieldName)
        val tvFieldType: TextView = view.findViewById(R.id.tvFieldType) // Voeg dit toe in je field_item.xml
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.field_item, parent, false)
        return FieldViewHolder(view)
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        val field = fields[position]
        holder.tvFieldName.text = field.naam
        holder.tvFieldType.text = field.type
        holder.itemView.setOnClickListener {
            itemClickListener(field)
        }
    }

    override fun getItemCount() = fields.size
}
