package com.sophiadiagrams.avedex.presentation.avedex


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.lib.models.Bird
import com.squareup.picasso.Picasso


class BirdsRVAdapter(
    private val birdList: List<Bird>, private val uid: String
) : RecyclerView.Adapter<BirdsRVAdapter.BirdViewHolder>() {

    var onItemClick: ((Bird) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avedex_item, parent, false)
        return BirdViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        holder.birdNameTV.text = birdList[position].name
        val imageUrl = "https://avedex.bepi.tech/birds/${birdList[position].name}.jpg"
        if (birdList[position].user == null) {
            holder.birdIV.setPadding(20, 20, 20, 20)
            holder.birdIV.setImageResource(R.drawable.ic_questionmark)
        } else
            Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ic_bird).into(holder.birdIV)
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

    inner class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdNameTV: TextView = itemView.findViewById(R.id.tv_birdName)
        val birdIV: ImageView = itemView.findViewById(R.id.iv_bird)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(birdList[bindingAdapterPosition])
            }
        }
    }
}