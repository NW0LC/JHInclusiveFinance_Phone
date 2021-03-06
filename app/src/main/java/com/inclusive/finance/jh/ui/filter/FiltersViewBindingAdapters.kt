/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inclusive.finance.jh.ui.filter

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.inclusive.finance.jh.R


@BindingAdapter("filterChipOnClick", "viewModel", requireAll = true)
fun filterChipOnClick(
    chip: Chip,
    filterChip: FilterChip,
    viewModel: FiltersViewModelDelegate
) {
    chip.setOnClickListener {
        viewModel.toggleFilter(filterChip.filter, !filterChip.isSelected,filterChip.isSingleCheck)
    }
}

@BindingAdapter("filterChipOnClose", "viewModel", requireAll = true)
fun filterChipOnClose(
    chip: Chip,
    filterChip: FilterChip,
    viewModel: FiltersViewModelDelegate
) {
    chip.setOnCloseIconClickListener {
        viewModel.toggleFilter(filterChip.filter, false)
    }
}

@BindingAdapter("filterChipText")
fun filterChipText(chip: Chip, filter: FilterChip) {
    if (filter.textResId != 0) {
        chip.setText(filter.textResId)
    } else {
        chip.text = filter.text
    }
}

@BindingAdapter("filterChipTint")
fun filterChipTint(chip: Chip, color: Int) {
    val tintColor = if (color != Color.TRANSPARENT) {
        color
    } else {
        ContextCompat.getColor(chip.context, R.color.MaterialPurple500)
    }
    chip.chipIconTint = ColorStateList.valueOf(tintColor)
}
@BindingAdapter("showResultCount", "resultCount", requireAll = true)
fun filterHeader(textView: TextView, showResultCount: Boolean?, resultCount: Int?) {
    if (showResultCount == true && resultCount != null) {
        textView.text = "??????????????????:$resultCount"
    } else {
        textView.text = "????????????"
    }
}