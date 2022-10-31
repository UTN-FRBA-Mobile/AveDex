package com.sophiadiagrams.avedex.presentation.avedex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentBirdDialogBinding
import com.sophiadiagrams.avedex.lib.models.Bird
import com.squareup.picasso.Picasso

class BirdDialogFragment(private val bird: Bird, private val birdImage: String) : DialogFragment() {

    private var _binding: FragmentBirdDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBirdDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvLocation.text = bird.discoveryLocation
            tvTime.text = bird.discoveryTime
            tvTitle.text = bird.name
            Picasso.get().load(birdImage).placeholder(R.drawable.ic_bird).into(ivBird)
            tvDescription.text =
                "Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn Descriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbnDescriptioasndaoskdnm;kasklhsa bdakshbd askhdbn ashdbjnsal fjhbnsalkjdn lasdjfn lakshabdnalskj dbnalkshbd alkhsjbdn laskdjfb nlaksdjbfnlasjdnfl ;oasjdnf kasjdfn lsjdnf ljsadn lk jnsdlfk dsjf jsa dflkdjbn"
        }
    }
}