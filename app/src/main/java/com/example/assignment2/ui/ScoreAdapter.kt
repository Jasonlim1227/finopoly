package com.example.assignment2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2.databinding.ItemScoreHistoryBinding
import com.example.assignment2.model.GameResultData

class ScoreAdapter(private val items: List<GameResultData>) :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(val binding: ItemScoreHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvDate.text = item.date
        holder.binding.tvScore.text = "Score: ${item.finalScore} pts"
        holder.binding.tvRank.text = item.performanceRank
        holder.binding.tvMoney.text = "RM ${item.finalMoney}"
        holder.binding.tvSavings.text = "RM ${item.totalSavings}"
        holder.binding.tvExpenses.text = "RM ${item.totalExpenses}"
    }

    override fun getItemCount(): Int = items.size
}