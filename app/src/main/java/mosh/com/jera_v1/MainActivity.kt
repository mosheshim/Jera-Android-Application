package mosh.com.jera_v1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
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

    private fun changeMenuItemVisibility(menu: Menu?, loggedIn: Boolean) {
        menu?.findItem(R.id.login_menu_item)?.isVisible = !loggedIn
        menu?.findItem(R.id.orders_menu_item)?.isVisible = loggedIn
        menu?.findItem(R.id.logout_menu_item)?.isVisible = loggedIn

    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (authRepo.isLoggedIn) changeMenuItemVisibility(menu, true)
        else changeMenuItemVisibility(menu, false)
        return super.onMenuOpened(featureId, menu)

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
            R.id.login_menu_item -> navController.navigate(R.id.navigation_login)
            R.id.logout_menu_item -> {
                authRepo.logout()
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show() //TODO make a string res
            }
            R.id.orders_menu_item ->
                Toast.makeText(this, "order page",Toast.LENGTH_SHORT).show()
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
        authRepo.destroyListeners()
    }


}