package mosh.com.jera_v1.ui.coffee_item

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentCoffeeItemScreenBinding
import mosh.com.jera_v1.utils.BaseFragment
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.buildPicasso
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.TextResource.Companion.asString


class CoffeeItemFragment() : BaseFragment<CoffeeItemViewModel>(), UiUtils {

    private var _binding: FragmentCoffeeItemScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProvider(this)[CoffeeItemViewModel::class.java]
        val coffeeId = arguments?.getString("id") //TODO find a place to put it (ID)
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
                buttonsRow.buttonAddToCart.text = addToCartButtonText.asString(resources)


                buildSpinner(
                    grindSizes,
                    spinnerGrindLevel
                ) { onGrindSizeClicked(it) }

                imageLayout.image.buildPicasso(
                    imageUrl,
                    imageLayout.progressBar
                )

                inputCoffeeQuantity.addTextChangedListener(
                    textWatcher {
                        inputCoffeeQuantityLayout.error = setQuantity(it)?.asString(resources)
                    })

                buttonsRow.buttonGoBack.setOnClickListener {
                    findNavController().popBackStack()
                }

                buttonsRow.buttonAddToCart.setOnClickListener {
                    viewModel.onAddToCartButtonClicked {
                        buildDialog(
                            getString(R.string.continue_shopping_dialog),
                            getString(R.string.yes),
                            getString(R.string.no)
                        ) { positiveClicked ->
                            findNavController().run {
                                popBackStack()
                                if (!positiveClicked) {
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

