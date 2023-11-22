import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.testtask.R
import com.example.testtask.ui.BookmarkScreenActivity
import com.example.testtask.ui.HomeScreenActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationHelper(private val context: Context, private val bottomNavigationView: BottomNavigationView) {

    init {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            handleNavigationItemSelected(item)
            true
        }
    }

    private fun handleNavigationItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_bookmark -> {
                navigateToBookmarkScreen(context)
            }
            R.id.menu_home -> {
                navigateToHomeScreen(context)
            }
        }
    }

    private fun navigateToBookmarkScreen(context: Context) {
        val intent = Intent(context, BookmarkScreenActivity::class.java)
        context.startActivity(intent)
        if (context is Activity) {
            context.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }

    private fun navigateToHomeScreen(context: Context) {
        val intent = Intent(context, HomeScreenActivity::class.java)
        context.startActivity(intent)
        if (context is Activity) {
            context.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}