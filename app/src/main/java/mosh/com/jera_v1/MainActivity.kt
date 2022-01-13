package mosh.com.jera_v1

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.cancel
import mosh.com.jera_v1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val authRepo = MyApplication.authRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

//        navController.addOnDestinationChangedListener { _, navD, _ ->
//            when (navD.id) {
//                R.id.navigation_coffee, R.id.navigation_tea
//                -> binding.navView.visibility = View.VISIBLE
//                else -> binding.navView.visibility = View.GONE
//            }
//        }

//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_coffee, R.id.navigation_tea
//            )
//        )
//        binding.navView.setupWithNavController(navController)

//        binding.navView.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.coffee_tab -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.nav_host_container, CoffeeFragment())
//                        .addToBackStack(null)
//                        .setReorderingAllowed(true)
//                        .commit()
//                    true
//                }
//                R.id.tea_tab -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.nav_host_container, TeaFragment())
//                        .addToBackStack(null)
//                        .setReorderingAllowed(true)
//                        .commit()
//                    true
//                }
//
//                else -> true
//            }
//        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart_button -> {
                navController.navigate(R.id.action_global_cart_fragment)
                return true
            }
            R.id.login_button -> {
                if (authRepo.isLoggedIn)
                    navController.navigate(R.id.navigation_profile)
                else
                    navController.navigate(R.id.action_global_login_fragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        navController.popBackStack()
        return super.onSupportNavigateUp()

    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.cartRepo.scope.cancel()
    }

}