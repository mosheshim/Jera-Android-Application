package mosh.com.jera_v1.ui.main_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import mosh.com.jera_v1.adapters.ViewPagerAdapter
import mosh.com.jera_v1.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private var _binding :FragmentViewPagerBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val pager  = binding.viewPager
        val tabLayout = binding.tabLayout

        pager.adapter = ViewPagerAdapter(
            listOf(CoffeeRecyclerViewFragment(), TeaRecyclerViewFragment()),
            childFragmentManager,
            lifecycle
        )

        TabLayoutMediator(
          tabLayout,
          pager
        ){tab, position ->
            when(position){
                0 -> tab.text = "Coffee"
                1 -> tab.text = "Tea"
            }
        }.attach()
        return binding.root

    }



    }
