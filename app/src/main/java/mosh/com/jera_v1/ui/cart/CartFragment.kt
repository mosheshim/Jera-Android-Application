package mosh.com.jera_v1.ui.cart

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.CartAdapter
import mosh.com.jera_v1.adapters.RecycleItemTouchHelper
import mosh.com.jera_v1.databinding.FragmentCartBinding
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.utils.BaseFragment
import mosh.com.jera_v1.utils.Utils.Companion.gone
import mosh.com.jera_v1.utils.Utils.Companion.visible

class CartFragment : BaseFragment<CartViewModel>() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter

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
            updateList()

        binding.buttonGoBackCartFrag.setOnClickListener {
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
                    buildDialog(
                        getString(R.string.login_to_check_out_message),
                        getString(R.string.log_in),
                        getString(R.string.cancel)
                    ) {
                        if (it) findNavController().navigate(R.id.action_global_login_fragment)
                    }
                else findNavController().navigate(R.id.navigate_to_checkout)
            }

            adapter = CartAdapter(viewModel.cart) { view, index ->
                showPopup(view, index)
            }
            val recyclerView = binding.rvCart

            ItemTouchHelper(RecycleItemTouchHelper(requireContext()) { index ->
                deleteItemDialog(index)
            })
                .attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.textTotalPrice.text =
                getString(R.string.money_symbol_with_string, viewModel.price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun deleteItemDialog(index: Int) =
        buildDialog(
            getString(R.string.delete_item_dialog),
            getString(R.string.delete),
            getString(R.string.cancel)
        ) {
            if (it) {
                viewModel.deleteItem(index)
                adapter.notifyItemRemoved(index)
                binding.textTotalPrice.text =
                    getString(R.string.money_symbol_with_string, viewModel.price)
            } else adapter.notifyItemChanged(index)
        }




    private fun showPopup(v: View, index: Int) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener {
                deleteItemDialog(index)
                true
            }
            inflate(R.menu.delete_cart_item)
            show()
        }

    }


}