package mosh.com.jera_v1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
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
    private val cartRepo = MyApplication.cartRepo

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
        val cartItem = menu!!.findItem(R.id.cart_button)
        cartItem.setActionView(R.layout.layout_toolbar_cart_icon)
        val badge = cartItem.actionView.findViewById<TextView>(R.id.cart_badge)
        cartItem.actionView.findViewById<FrameLayout>(R.id.cart_toolbar_icon).setOnClickListener {
            navController.navigate(R.id.action_global_cart_fragment)
        }
        cartRepo.cartLiveData.observe(this){ badge.text = it.size.toString()}

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println(item.itemId)
        when (item.itemId) {
            R.id.login_menu_item -> navController.navigate(R.id.navigation_login)
            R.id.logout_menu_item -> {
                authRepo.logout()
                Toast.makeText(this, R.string.signed_out, Toast.LENGTH_SHORT).show()
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
        cartRepo.scope.cancel()
        authRepo.destroyListeners()
    }


}