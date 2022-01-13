package mosh.com.jera_v1.ui.coffee

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentCoffeeItemScreenBinding
import mosh.com.jera_v1.utils.Listeners
import mosh.com.jera_v1.utils.UiUtils


class CoffeeItemFragment() : Fragment() {

    private lateinit var viewModel: CoffeeItemViewModel
    private var _binding: FragmentCoffeeItemScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProvider(requireActivity())[CoffeeItemViewModel::class.java]
        val coffeeId = arguments?.getString("id")
        viewModel.setCoffeeById(coffeeId!!)

        _binding = FragmentCoffeeItemScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            binding.apply {
                viewModel.apply {
                    titleRow.textName.text = name
                    titleRow.textSubTitle.text = roastingLevel
                    titleRow.textPrice.text = price

                    ratingBody.textTaste.setText(R.string.body_label)
                    ratingBody.rating.rating = bodyRating

                    ratingBitterness.textTaste.setText(R.string.bitterness_label)
                    ratingBitterness.rating.rating = bitternessRating

                    ratingSweetness.textTaste.setText(R.string.sweetness_label)
                    ratingSweetness.rating.rating = sweetnessRating

                    ratingAcidity.textTaste.setText(R.string.acidity_label)
                    ratingAcidity.rating.rating = acidityRating

                    textCoffeeOriginCountry.text = countryOfOrigin
                    textCoffeeTasteProfile.text = tasteProfile
                    textDescription.text = description
                    textCoffeeStock.text = stockState


                    UiUtils.buildSpinner(
                        requireContext(),
                        grindSizes,
                        spinnerGrindLevel
                    ) { setGrindSize(it) }

                    UiUtils.buildPicasso(
                        imageUrl,
                        imageLayout.image,
                        imageLayout.progressBar
                    )

                    inputCoffeeQuantity.addTextChangedListener(
                        Listeners.textWatcher {
                            inputCoffeeQuantityLayout.error = setQuantity(it)
                        })

                    buttonsRow.buttonGoBack.setOnClickListener {
                        findNavController().popBackStack()
                    }

                    buttonsRow.buttonAddToCart.setOnClickListener {
                        viewModel.addToCart(){
                            UiUtils.addToCartDialog(requireContext()) { positiveClicked ->
                                findNavController().run {
                                    popBackStack()
                                    if (positiveClicked) {
                                        navigate(R.id.navigation_cart)
                                    }
                                }
                            }
                        }

                }
            }
        }

    }

}

