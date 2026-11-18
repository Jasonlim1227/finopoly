package com.example.assignment2.data

import android.content.Context
import com.example.assignment2.model.GameResultData
import org.json.JSONArray
import org.json.JSONObject

object ScoreRepository {
    private const val PREF_NAME = "finopoly_prefs"
    private const val KEY_SCORE_HISTORY = "score_history"

    fun saveGameResult(context: Context, result: GameResultData) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val historyList = getGameHistory(context).toMutableList()
        historyList.add(0, result)

        val jsonArray = JSONArray()
        historyList.take(20).forEach { item ->
            val jsonObject = JSONObject().apply {
                put("id", item.id)
                put("date", item.date)
                put("finalMoney", item.finalMoney)
                put("totalSavings", item.totalSavings)
                put("totalExpenses", item.totalExpenses)
                put("finalScore", item.finalScore)
                put("performanceRank", item.performanceRank)
            }
            jsonArray.put(jsonObject)
        }

        prefs.edit().putString(KEY_SCORE_HISTORY, jsonArray.toString()).apply()
    }

    fun getGameHistory(context: Context): List<GameResultData> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_SCORE_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<GameResultData>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GameResultData(
                        id = obj.optLong("id", System.currentTimeMillis()),
                        date = obj.optString("date", "Unknown Date"),
                        finalMoney = obj.optInt("finalMoney", 0),
                        totalSavings = obj.optInt("totalSavings", 0),
                        totalExpenses = obj.optInt("totalExpenses", 0),
                        finalScore = obj.optInt("finalScore", 0),
                        performanceRank = obj.optString("performanceRank", "Budgeter")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun getHighScore(context: Context): Int {
        val history = getGameHistory(context)
        return history.maxOfOrNull { it.finalScore } ?: 0
    }
}