package io.numbers.mediant.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.numbers.mediant.R
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.viewmodel.EventObserver
import kotlinx.android.synthetic.main.activity_base.*
import org.koin.android.viewmodel.ext.android.viewModel

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in BaseActivity.onCreate() method.
class BaseActivity : AppCompatActivity(), ShowableSnackbar by DefaultShowableSnackbar() {

    private val viewModel: BaseViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()

        setContentView(R.layout.activity_base)

        initToolbar()

        viewModel.handleIntent(intent)
    }

    private fun initViewModel() {
        findViewById<View?>(android.R.id.content)?.also { view ->
            viewModel.showSnackbar.observe(this, EventObserver { showSnackbar(view, it) })
            viewModel.showErrorSnackbar.observe(this, EventObserver { showErrorSnackbar(view, it) })
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.mainFragment) toolbar.visibility = View.VISIBLE
                if (destination.id == R.id.onboardingFragment) toolbar.visibility = View.GONE
            }
        }
        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.initializationFragment,
                    R.id.mainFragment,
                    R.id.onboardingFragment
                )
            )
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.handleIntent(intent)
    }
}