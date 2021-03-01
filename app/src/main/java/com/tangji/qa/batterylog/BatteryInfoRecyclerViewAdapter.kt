package com.tangji.qa.batterylog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BatteryInfoRecyclerViewAdapter(private var mutableMap: MutableMap<String,String>,private var listname:List<String>,private var listname2:List<String>):
    RecyclerView.Adapter<BatteryInfoRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var value: TextView = itemView.findViewById<View>(R.id.textView_battery_info_v) as TextView
        var name: TextView = itemView.findViewById<View>(R.id.textView_battery_info_t) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.battery_info_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text=listname2[position]
        holder.value.text=mutableMap[listname[position]]
    }

    override fun getItemCount(): Int {
        return mutableMap.size
    }

}