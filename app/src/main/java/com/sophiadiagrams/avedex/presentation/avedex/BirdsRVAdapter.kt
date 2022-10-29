package com.sophiadiagrams.avedex.presentation.avedex


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.lib.models.Bird
import com.squareup.picasso.Picasso

class BirdsRVAdapter(private val birdList: List<Bird>,
                     private val context: Context
) : RecyclerView.Adapter<BirdsRVAdapter.BirdViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BirdViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.avedex_item,
            parent, false
        )
        return BirdViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BirdsRVAdapter.BirdViewHolder, position: Int) {
        holder.birdNameTV.text = birdList[position].name
        Picasso.get().load(birdList[position].image).into(holder.birdIV)
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

    class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdNameTV: TextView = itemView.findViewById(R.id.tv_birdName)
        val birdIV: ImageView = itemView.findViewById(R.id.iv_bird)
    }
}