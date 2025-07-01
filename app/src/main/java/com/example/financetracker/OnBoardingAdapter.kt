package com.example.financetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnBoardingAdapter(private val context: Context) :
    RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    private val layouts = arrayOf(
        R.layout.onboarding_screen1,
        R.layout.onboarding_screen2,
        R.layout.onboarding_screen3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
    override fun getItemCount() = layouts.size
    override fun getItemViewType(position: Int): Int = position

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
