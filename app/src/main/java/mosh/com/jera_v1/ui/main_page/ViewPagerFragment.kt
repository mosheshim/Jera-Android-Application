package mosh.com.jera_v1.ui.main_page

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.ViewPagerAdapter
import mosh.com.jera_v1.databinding.FragmentViewPagerBinding
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.gone
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.visible

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var  toolBar:Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar = requireActivity().findViewById(R.id.main_toolbar)
        updateUi()
        binding.buttonReload.setOnClickListener {
        updateUi()
        }

    }

    private fun updateUi() {
        if (checkIfConnected()){
            buildViewPager()
            toolBar.visible()
        }
        else {
            binding.noInternetContainer.visible()
            toolBar.gone()

            Toast.makeText(
                requireContext(), getString(R.string.no_internet_message), Toast.LENGTH_LONG).show()
        }

    }

    private fun buildViewPager() {
        val pager = binding.viewPager
        val tabLayout = binding.tabLayout

        binding.noInternetContainer.gone()
        pager.adapter = ViewPagerAdapter(
            listOf(CoffeeRecyclerViewFragment(), TeaRecyclerViewFragment()),
            childFragmentManager,
            lifecycle
        )
        TabLayoutMediator(
            tabLayout,
            pager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.coffee)
                1 -> tab.text = getString(R.string.tea)
            }
        }.attach()
    }

    private fun checkIfConnected():Boolean{
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }



}
