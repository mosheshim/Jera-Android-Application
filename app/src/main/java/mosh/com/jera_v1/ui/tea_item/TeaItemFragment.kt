package mosh.com.jera_v1.ui.tea_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentTeaItemScreenBinding
import mosh.com.jera_v1.inheritance.fragments.BaseFragment
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.buildPicasso
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.visible
import mosh.com.jera_v1.inheritance.fragments.ID
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.TextResource.Companion.asString

class TeaItemFragment : BaseFragment<TeaItemViewModel>(), UiUtils {

    private var _binding: FragmentTeaItemScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            (ViewModelProvider(this)[TeaItemViewModel::class.java])
        val productLineId = arguments?.getString(ID)
        viewModel.setProductLineById(productLineId!!)

        _binding = FragmentTeaItemScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//----------------------------------------init UI-------------------------------------------------//
        binding.apply {
            inputTeaQuantity.addTextChangedListener(
                textWatcher {
                    inputTeaQuantityLayout.error = viewModel.setQuantity(it)?.asString(resources)
                })

            buttonsRow.buttonGoBack.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonsRow.buttonAddToCart.setOnClickListener {
                viewModel.onAddToCartButtonClicked{
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

            textProductLineDescription.text = viewModel.productLineDescription
            titleRow.textSubTitle.text = viewModel.subTitle.asString(resources)
            containerOptions.visibility = viewModel.containerOptionVisibility

            imageLayout.image.buildPicasso(viewModel.imageURL, imageLayout.progressBar)
            titleRow.textName.text = viewModel.name
            titleRow.textPrice.text = getString(R.string.money_symbol_with_string, viewModel.price)

           buildSpinner(
                viewModel.teaListNames,
               spinnerTeaOption
            ) { viewModel.onTeaClicked(it) }
            spinnerTeaOption.setOnClickListener{hideKeyBoard()}


            //-------------------------------tea observer-----------------------------------------//
            viewModel.tea.observe(viewLifecycleOwner) {
                containerQuantity.visible()

                containerSubDescription.visible()
                textSubDescription.text = viewModel.teaDescription

                buttonsRow.buttonAddToCart.text = viewModel.addToCartButtonText.asString(resources)
                imageLayout.image.buildPicasso(viewModel.imageURL , imageLayout.progressBar)
                titleRow.textName.text = viewModel.name
                titleRow.textPrice.text = getString(R.string.money_symbol_with_string, viewModel.price)
                //------------------------------weight container----------------------------------//
                containerWeight.visibility = viewModel.containerWeightVisibility
                spinnerTeaWeight.setText(getString(
                    R.string.weight_symbol,
                    viewModel.firstWeightName.asString(resources)
                ))

                buildSpinner(
                    getWeightsWithWeightSymbol(viewModel.weightListNames),
                    spinnerTeaWeight
                ) {
                    viewModel.setWeight(it)
                    titleRow.textPrice.text = getString(R.string.money_symbol_with_string, viewModel.price)
                }
                spinnerTeaWeight.setOnClickListener { hideKeyBoard() }
                startContainerAnimation()
            }
        }
    }

    //-------------------------------------animations---------------------------------------------//
    /**
     * Start container animations depending on which widget is visible
     */
    private fun startContainerAnimation() {
        val containerAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.conatiner_animation)
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.apply {
            containerOptions.startAnimation(containerAnimation)
            containerQuantity.startAnimation(containerAnimation)
            containerSubDescription.startAnimation(containerAnimation)

            imageLayout.image.takeUnless { viewModel.isOneTea }?.startAnimation(fadeInAnimation)
            containerWeight.takeIf { it.visibility == View.VISIBLE }
                ?.startAnimation(containerAnimation)
            buttonsRow.containerButtons.startAnimation(containerAnimation)
        }
    }

    /**
     * Added a weight symbol (like g) to the end of each weight
     */
    private fun getWeightsWithWeightSymbol(lst:List<String>):List<String>{
        val weightWithSymbol = mutableListOf<String>()
        for (weight in lst){
            weightWithSymbol.add(getString(R.string.weight_symbol, weight))
        }
        return weightWithSymbol
    }

}
