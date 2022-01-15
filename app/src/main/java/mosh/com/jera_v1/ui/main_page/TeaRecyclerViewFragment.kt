package mosh.com.jera_v1.ui.main_page

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.TeaAdapter
import mosh.com.jera_v1.databinding.FragmentTeaBinding
import mosh.com.jera_v1.utils.Utils.Companion.getSpanNum

class TeaRecyclerViewFragment : Fragment() {

    private lateinit var viewModel: ProductsViewModel
    private var _binding: FragmentTeaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(requireActivity())[ProductsViewModel::class.java]

        _binding = FragmentTeaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.productLinesLiveData.observe(viewLifecycleOwner) {
            binding.rvTea.adapter = TeaAdapter(it) { id ->
                findNavController().navigate(
                    R.id.navigation_tea_Item_screen,
                    bundleOf("id" to id)
                )

            }
            binding.rvTea.layoutManager = GridLayoutManager(
                requireContext(), getSpanNum(resources)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}