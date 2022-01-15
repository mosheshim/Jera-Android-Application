package mosh.com.jera_v1.ui.main_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.CoffeeAdapter
import mosh.com.jera_v1.databinding.FragmentCoffeeBinding
import mosh.com.jera_v1.utils.Utils.Companion.getSpanNum
import mosh.com.jera_v1.utils.Utils.Companion.gone

class CoffeeRecyclerViewFragment : Fragment() {

    private lateinit var viewModel: ProductsViewModel

    private var _binding: FragmentCoffeeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel =
            ViewModelProvider(requireActivity())[ProductsViewModel::class.java]

        _binding = FragmentCoffeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.coffeeList.observe(viewLifecycleOwner) {
            binding.progressBar.gone()
            binding.rvCoffee.adapter = CoffeeAdapter(it) { id ->
                findNavController().navigate(
                    R.id.navigation_coffee_Item_screen,
                    bundleOf("id" to id)
                )
            }
            binding.rvCoffee.layoutManager = GridLayoutManager(
                requireContext(), getSpanNum(resources)
            )
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}