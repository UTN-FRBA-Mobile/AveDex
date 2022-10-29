package com.sophiadiagrams.avedex.presentation.bird_recognized

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.sophiadiagrams.avedex.R

class BirdRecognizedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_bird_recognized, container, false)

        view.findViewById<Button>(R.id.btn_goToBirdDescription).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_birdRecognized_to_birdDescription)
        }

        return view
    }
}