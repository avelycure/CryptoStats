package com.avelycure.cryptostats.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.domain.Trade
import java.text.SimpleDateFormat
import java.util.*

class TradeAdapter(
    val tradeList: List<Trade>
) : RecyclerView.Adapter<TradeAdapter.TradeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        return TradeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.trade_item, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        holder.date.text = unixTimeToStringDate(tradeList[position].timestampms)
        holder.id.text = tradeList[position].tid.toString()
        holder.price.text = tradeList[position].price.toString()
        holder.type.text = tradeList[position].type
        holder.amount.text = tradeList[position].amount.toString()
    }

    override fun getItemCount() = tradeList.size

    class TradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: AppCompatTextView = itemView.findViewById(R.id.trade_date)
        val id: AppCompatTextView = itemView.findViewById(R.id.trade_id)
        val price: AppCompatTextView = itemView.findViewById(R.id.trade_price)
        val amount: AppCompatTextView = itemView.findViewById(R.id.trade_amount)
        val type: AppCompatTextView = itemView.findViewById(R.id.trade_type)
    }

    fun unixTimeToStringDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(timestamp)
        return sdf.format(netDate)
    }

}