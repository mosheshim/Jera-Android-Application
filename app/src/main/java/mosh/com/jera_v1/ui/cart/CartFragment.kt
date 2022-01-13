package mosh.com.jera_v1.ui.cart

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.CartAdapter
import mosh.com.jera_v1.adapters.RecycleItemTouchHelper
import mosh.com.jera_v1.databinding.FragmentCartBinding
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.UiUtils.Companion.gone
import mosh.com.jera_v1.utils.UiUtils.Companion.visible

class CartFragment : Fragment() {

    private lateinit var viewModel: CartViewModel
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[CartViewModel::class.java]
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewModel.onCartLoad {
                binding.progressBar.gone()
                updateList()
            }


        binding.buttonGoBackCartFrag.setOnClickListener{
                findNavController().popBackStack()
        }

    }

    /**
     * Update the view when called.
     * Very important to call only after the cart in viewModel is updated to prevent crashes
     */
    private fun updateList() {
        if (viewModel.cartIsEmpty) binding.textCartEmpty.visible()

        else {
            binding.buttonGoToPayment.setOnClickListener {
//            TODO delete backstack later
                if (!viewModel.isLoggedIn())
                    UiUtils.buildDialog(
                        requireContext(),
                        getString(R.string.login_to_check_out_meassage),
                        getString(R.string.log_in),
                        getString(R.string.cancel)
                    ) {
                        if (it) findNavController().navigate(R.id.action_global_login_fragment)
                    }
                else findNavController().navigate(R.id.navigate_to_checkout)
            }

            val adapter = CartAdapter(viewModel.cart)
            val recyclerView = binding.rvCart

            ItemTouchHelper(RecycleItemTouchHelper(requireContext()) { index ->
                UiUtils.deleteItemDialog(requireContext()) {
                    if (it) {
                        viewModel.deleteItem(index)
                        adapter.notifyItemRemoved(index)
                        binding.textTotalPrice.text =
                            getString(R.string.money_symbol,viewModel.price)
                    } else adapter.notifyItemChanged(index)
                }
            })
                .attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.textTotalPrice.text = getString(R.string.money_symbol,viewModel.price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}