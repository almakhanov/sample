package kz.dodix.sample.extensions

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

fun ImageView.loadImage(url: String, fragment: Fragment) {
    Glide.with(fragment)
        .load(url)
        .into(this)
}
