package com.example.zbporn.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView

object Animator {

    fun animateRecyclerView(hold: RecyclerView.ViewHolder, down: Boolean) {
        val animator = ObjectAnimator.ofFloat(hold.itemView, "TranslationX", -100f, 0f)
        animator.setInterpolator(OvershootInterpolator())

        val animator1 = ObjectAnimator.ofFloat(hold.itemView, "TranslationX", 100f, 0f)
        animator1.setInterpolator(OvershootInterpolator())

        val animator2 =
            ObjectAnimator.ofFloat(hold.itemView, "TranslationY", if (down) 120f else -120f, 0f)
        animator.setInterpolator(OvershootInterpolator())

        val animator3 = ObjectAnimator.ofFloat(hold.itemView, "ScaleX", 0.9f, 1f)
        animator3.setInterpolator(AnticipateOvershootInterpolator())

        val animator4 = ObjectAnimator.ofFloat(hold.itemView, "ScaleY", 0.9f, 1f)
        animator4.setInterpolator(AnticipateOvershootInterpolator())

        val set = AnimatorSet()
        set.playTogether(animator2, animator3, animator4)
        set.duration = 1000
        set.start()
    }

}