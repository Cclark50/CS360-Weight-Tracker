package com.snhu.ProjectTwo.utilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.databinding.WeightDataCardBinding

//@Author Christian Clark
//@Date 1-9-26

/* This class if for the cards that show up in the list fragment
** Each card contains the weight and date of the record
** This is used for the recycler view in the list fragment
*/

class WeightCardAdapter(
    private val _context: Context,
    private var _items: MutableList<UserInfo>,
    private val _id: Long,
    private val _onDelete: (info: UserInfo, position: Int) -> Unit,
    private val _onDate: (info: UserInfo, position: Int) -> Unit
): RecyclerView.Adapter<WeightCardAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val _dateText = itemView.findViewById<TextView>(R.id.card_date_text)
        val _weightText = itemView.findViewById<TextView>(R.id.card_weight_text)
        val _deleteButton = itemView.findViewById<ImageButton>(R.id.card_delete_button)

        fun bind(item: UserInfo){
            _dateText.setText(item.getDateString())
            _weightText.setText(item.weight.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.weight_data_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: UserInfo = _items.get(position)
        holder._deleteButton.setOnClickListener { _onDelete(item, position) }
        holder._dateText.setOnClickListener { _onDate(item, position) }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    public fun updateData(newInfo: List<UserInfo>){
        _items.clear()
        _items.addAll(newInfo)
        notifyDataSetChanged()
    }
}