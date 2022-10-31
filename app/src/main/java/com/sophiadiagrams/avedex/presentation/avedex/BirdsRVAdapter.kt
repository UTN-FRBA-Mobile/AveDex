package com.sophiadiagrams.avedex.presentation.avedex


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.lib.models.Bird
import com.squareup.picasso.Picasso

class BirdsRVAdapter(
    private val birdList: List<Bird>, private val uid: String, private val storage: FirebaseStorage
) : RecyclerView.Adapter<BirdsRVAdapter.BirdViewHolder>() {

    var onItemClick: ((Bird) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avedex_item, parent, false)
        return BirdViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        holder.birdNameTV.text = birdList[position].name
        storage.reference.child("$uid/${birdList[position].name}").downloadUrl.addOnCompleteListener {
            if (it.isSuccessful)
                Picasso.get()
                    //.load(it.result)
                    .load("https://firebasestorage.googleapis.com/v0/b/avedex-1915b.appspot.com/o/028.jpg?alt=media&token=5a28992c-5ec8-4d98-b167-90d211a72f0f")
                    .placeholder(R.drawable.ic_bird).into(holder.birdIV)
        }
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

    inner class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdNameTV: TextView = itemView.findViewById(R.id.tv_birdName)
        val birdIV: ImageView = itemView.findViewById(R.id.iv_bird)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(birdList[adapterPosition])
            }
        }
    }
}