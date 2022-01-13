package mosh.com.jera_v1.ui.tea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentTeaItemScreenBinding
import mosh.com.jera_v1.utils.Listeners
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.UiUtils.Companion.visible

class TeaItemFragment : Fragment() {

    private lateinit var viewModel: TeaItemViewModel
    private var _binding: FragmentTeaItemScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[TeaItemViewModel::class.java]
        val productLineId = arguments?.getString("id")
        viewModel.setProductLineById(productLineId!!)

        _binding = FragmentTeaItemScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//----------------------------------------init UI-------------------------------------------------//
        binding.apply {
            inputTeaQuantity.addTextChangedListener(
                Listeners.textWatcher {
                    inputTeaQuantityLayout.error = viewModel.setQuantity(it)
                })

            buttonsRow.buttonGoBack.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonsRow.buttonAddToCart.setOnClickListener {
                viewModel.addToCart() {
                    UiUtils.addToCartDialog(requireContext()) { goToCart ->
                        findNavController().run {
                            popBackStack()
                            if (goToCart) navigate(R.id.navigation_cart)
                        }
                    }
                }
            }

            textProductLineDescription.text = viewModel.productLineDescription
            titleRow.textSubTitle.text = viewModel.subTitle
            containerOptions.visibility = viewModel.containerOptionVisibility

            loadImage(viewModel.imageURL)
            titleRow.textName.text = viewModel.name
            titleRow.textPrice.text = viewModel.price

            UiUtils.buildSpinner(
                requireContext(),
                viewModel.teaListNames,
                textTeaOption
            ) { viewModel.setTea(it) }

            //-------------------------------tea observer-----------------------------------------//
            viewModel.tea.observe(viewLifecycleOwner) {
                containerQuantity.visible()

                containerSubDescription.visible()
                textSubDescription.text = viewModel.teaDescription

                textTeaStock.text = viewModel.inStock
                loadImage(viewModel.imageURL)
                titleRow.textName.text = viewModel.name
                titleRow.textPrice.text = viewModel.price
                //------------------------------weight container----------------------------------//
                containerWeight.visibility = viewModel.containerWeightVisibility
                textTeaWeight.setText(viewModel.firstWeightName)

                UiUtils.buildSpinner(
                    requireContext(),
                    viewModel.weightListNames,
                    textTeaWeight
                ) {
                    viewModel.setWeight(it)
                    titleRow.textPrice.text = viewModel.price
                }
                startContainerAnimation()
            }
        }
    }

    //-------------------------------------functions----------------------------------------------//
    private fun loadImage(uri: String) {
        UiUtils.buildPicasso(uri, binding.imageLayout.image, binding.imageLayout.progressBar)
    }

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

}
