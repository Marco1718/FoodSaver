package com.foodsaver.ui

import android.view.View
import android.widget.TextView
import com.foodsaver.R
import com.foodsaver.data.Estado

/** Pinta el TextView con id tvBadge (dentro de item_alimento) según el estado. */
fun bindBadge(itemView: View, estado: Estado) {
    val tv = itemView.findViewById<TextView>(R.id.tvBadge)
    tv.text = estado.texto
    val ctx = itemView.context
    when (estado) {
        Estado.URGENTE -> {
            tv.setBackgroundResource(R.drawable.bg_badge)
            tv.backgroundTintList = ctx.getColorStateList(R.color.badge_urgent_bg)
            tv.setTextColor(ctx.getColor(R.color.badge_urgent_text))
        }
        Estado.PROXIMO -> {
            tv.setBackgroundResource(R.drawable.bg_badge)
            tv.backgroundTintList = ctx.getColorStateList(R.color.badge_soon_bg)
            tv.setTextColor(ctx.getColor(R.color.badge_soon_text))
        }
        Estado.NORMAL -> {
            tv.setBackgroundResource(R.drawable.bg_badge)
            tv.backgroundTintList = ctx.getColorStateList(R.color.badge_normal_bg)
            tv.setTextColor(ctx.getColor(R.color.badge_normal_text))
        }
    }
}
