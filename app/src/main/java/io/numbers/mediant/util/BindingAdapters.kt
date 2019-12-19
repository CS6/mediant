package io.numbers.mediant.util

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:bitmap")
fun setBitmap(imageView: ImageView, bitmap: Bitmap?) {
    imageView.setImageBitmap(bitmap)
}