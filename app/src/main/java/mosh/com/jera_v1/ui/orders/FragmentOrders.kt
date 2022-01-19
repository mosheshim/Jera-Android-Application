package mosh.com.jera_v1.ui.orders

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mosh.com.jera_v1.databinding.FragmentOrdersBinding
import mosh.com.jera_v1.utils.BaseFragment

class FragmentOrders : BaseFragment<OrdersViewModel>() {

    private var _binding : FragmentOrdersBinding? = null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.orders.observe(viewLifecycleOwner ){
            println(it.size)
        }
    }



}