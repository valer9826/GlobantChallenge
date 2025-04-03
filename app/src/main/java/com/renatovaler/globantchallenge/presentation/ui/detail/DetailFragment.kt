package com.renatovaler.globantchallenge.presentation.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.renatovaler.globantchallenge.MainActivity
import com.renatovaler.globantchallenge.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var _binding: FragmentDetailBinding
    private val binding get() = _binding

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpUi()
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            val activity = (activity as? MainActivity)
            activity?.setupToolbar(this)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.toolbarTitle.text = args.country.officialName
    }

    private fun setUpUi() {
        val country = args.country

        with(binding) {
            tvOfficialName.text = country.officialName
            tvCommonName.text = country.commonName
            tvCapital.text = country.capital
            tvRegion.text = country.region
            tvSubregion.text = country.subregion
            tvPopulation.text = country.population
            tvLanguages.text = country.languages
            tvCurrencies.text = country.currencies
            tvCarSide.text = country.carSide

            imageFlag.load(country.flagUrl) {
                crossfade(true)
//                placeholder(R.drawable.)
//                error(R.drawable.)
            }

            imageCoatOfArms.load(country.coatOfArmsUrl) {
                crossfade(true)
//                placeholder(R.drawable.)
//                error(R.drawable.)
            }
        }
    }

}