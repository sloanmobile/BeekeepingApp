package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Composable function for displaying a row of selectable filter chips for data entry.
 *
 * @param enumClass A Class object representing the enumeration class defining selectable values.
 * @param selectedValue The currently selected enumeration value.
 * @param onChipSelected Callback invoked when a filter chip is selected.
 * @param title The title or label displayed above the filter chips.
 * @param icon The icon displayed to the left of the title.
 * @param divider The divider displayed above and below the filter chips.
 * Defaults to only displaying a divider below the filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T : Enum<T>> DataEntryChip(
    enumClass: Class<T>,
    selectedValue: T?,
    onChipSelected: (T?) -> Unit,
    title: String? = null,
    icon: @Composable (() -> Unit)? = null,
    divider: Divider = Divider(top = false, bottom = true),
) {
    if (divider.top) HorizontalDivider()
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
            .padding(4.dp)
    ) {
        TitleForChip(title, icon)
        Row(
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(48.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                horizontalItemSpacing = 4.dp,
            ) {
                items(enumClass.enumConstants?.size ?: 0) { index ->
                    val enumValue = enumClass.enumConstants?.get(index)
                    FilterChip(
                        selected = selectedValue == enumValue,
                        onClick = {
                            onChipSelected(enumValue)
                        },
                        label = {
                            val firstPropertyName = enumClass.declaredFields.first().name
                            val field = enumValue!!.javaClass.getDeclaredField(firstPropertyName)
                            field.isAccessible = true
                            val displayValue = field.get(enumValue) as String
                            Text(text = displayValue)
                        }
                    )
                }
            }
        }
    }
    if (divider.bottom) HorizontalDivider()
}

/**
 * Composable function for displaying a row of selectable filter chips for data entry using a list of string values.
 *
 * @param stringValues The list of string values to be displayed as filter chips.
 * @param selectedValue The currently selected string value.
 * @param onChipSelected Callback invoked when a filter chip is selected.
 * @param title The title or label displayed above the filter chips.
 * @param icon The icon displayed to the left of the title.
 * @param divider The divider displayed above and below the filter chips.
 * Defaults to only displaying a divider below the filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DataEntryChip(
    stringValues: List<String>,
    selectedValue: String?,
    onChipSelected: (String?) -> Unit,
    title: String? = null,
    icon: @Composable (() -> Unit)? = null,
    divider: Divider = Divider(top = false, bottom = true),
) {
    if (divider.top) HorizontalDivider()
    Column(horizontalAlignment = Alignment.Start) {
        TitleForChip(title, icon)
        Row(
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(48.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                horizontalItemSpacing = 4.dp,
            ) {
                items(stringValues) { value ->
                    FilterChip(
                        selected = selectedValue == value,
                        onClick = {
                            onChipSelected(value)
                        },
                        label = {
                            Text(text = value)
                        }
                    )
                }
            }
        }
    }
    if (divider.bottom) HorizontalDivider()
}

@Composable
fun TitleForChip(title: String?, icon: @Composable() (() -> Unit)?) {
    if (title != null) {
        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
            icon?.invoke()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
            )
        }
    }
}

/**
 * Composable function for displaying a row of selectable filter chips for data entry using a list of string values.
 *
 * @param stringValues The list of string values to be displayed as filter chips.
 * @param selectedValues The list of currently selected string values.
 * @param onChipSelected Callback invoked when filter chips are selected.
 * @param title The title or label displayed above the filter chips.
 * @param icon The icon displayed to the left of the title.
 * @param divider The divider displayed above and below the filter chips.
 * Defaults to only displaying a divider below the filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MultiDataEntryChip(
    stringValues: List<String>,
    selectedValues: List<String>,
    onChipSelected: (List<String>) -> Unit,
    title: String? = null,
    icon: @Composable (() -> Unit)? = null,
    divider: Divider = Divider(top = false, bottom = true),
) {
    if (divider.top) HorizontalDivider()
    Column(horizontalAlignment = Alignment.Start) {

        TitleForChip(title, icon)
        Row(
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(48.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                horizontalItemSpacing = 4.dp,
            ) {
                items(stringValues) { value ->
                    FilterChip(
                        selected = selectedValues.contains(value),
                        onClick = {
                            val updatedSelection = if (selectedValues.contains(value)) {
                                selectedValues.filter { it != value }
                            } else {
                                selectedValues + value
                            }
                            onChipSelected(updatedSelection)
                        },
                        label = {
                            Text(text = value)
                        }
                    )
                }
            }
        }
    }
    if (divider.bottom) HorizontalDivider()
}

/**
 * Composable function for displaying a row of selectable filter chips for data entry.
 *
 * @param enumClass A Class object representing the enumeration class defining selectable values.
 * @param selectedValues The list of currently selected enumeration values.
 * @param onChipSelected Callback invoked when filter chips are selected.
 * @param title The title or label displayed above the filter chips.
 * @param icon The icon displayed to the left of the title.
 * @param divider The divider displayed above and below the filter chips.
 * Defaults to only displaying a divider below the filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T : Enum<T>> DataEntryChip(
    enumClass: Class<T>,
    selectedValues: List<T>,
    onChipSelected: (List<T?>) -> Unit,
    title: String? = null,
    icon: @Composable (() -> Unit)? = null,
    divider: Divider = Divider(top = false, bottom = true),
) {
    if (divider.top) HorizontalDivider()
    Column(horizontalAlignment = Alignment.Start) {
        TitleForChip(title = title, icon = icon)
        Row(
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(48.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                horizontalItemSpacing = 4.dp,
            ) {
                items(enumClass.enumConstants?.size ?: 0) { index ->
                    val enumValue = enumClass.enumConstants?.get(index)
                    FilterChip(
                        selected = selectedValues.contains(enumValue),
                        onClick = {
                            val updatedSelection = if (selectedValues.contains(enumValue)) {
                                selectedValues.filter { it != enumValue }
                            } else {
                                selectedValues + enumValue
                            }
                            onChipSelected(updatedSelection)
                        },
                        label = {
                            val firstPropertyName = enumClass.declaredFields.first().name
                            val field = enumValue!!.javaClass.getDeclaredField(firstPropertyName)
                            field.isAccessible = true
                            val displayValue = field.get(enumValue) as String
                            Text(text = displayValue)
                        }
                    )
                }
            }
        }
    }
    if (divider.bottom) HorizontalDivider()
}

/**
 * Composable function for displaying a row of selectable filter chips for data entry.
 *
 * @param enumClass A Class object representing the enumeration class defining selectable values.
 * @param selectedValues The list of currently selected enumeration values.
 * @param onChipSelected Callback invoked when filter chips are selected.
 * @param title The title or label displayed above the filter chips.
 * @param icon The icon displayed to the left of the title.
 * @param divider The divider displayed above and below the filter chips.
 * Defaults to only displaying a divider below the filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T : Enum<T>> MultiDataEntryChip(
    enumClass: Class<T>,
    selectedValues: List<T?>,
    onChipSelected: (List<T?>) -> Unit,
    title: String? = null,
    icon: @Composable (() -> Unit)? = null,
    divider: Divider = Divider(top = false, bottom = true),
) {
    if (divider.top) HorizontalDivider()
    Column(horizontalAlignment = Alignment.Start) {
        TitleForChip(title = title, icon = icon)
        Row(
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(48.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                horizontalItemSpacing = 4.dp,
            ) {
                items(enumClass.enumConstants?.size ?: 0) { index ->
                    val enumValue = enumClass.enumConstants?.get(index)
                    FilterChip(
                        selected = selectedValues.contains(enumValue),
                        onClick = {
                            val updatedSelection = if (selectedValues.contains(enumValue)) {
                                selectedValues.filter { it != enumValue }
                            } else {
                                selectedValues + enumValue
                            }
                            onChipSelected(updatedSelection)
                        },
                        label = {
                            val firstPropertyName = enumClass.declaredFields.first().name
                            val field = enumValue!!.javaClass.getDeclaredField(firstPropertyName)
                            field.isAccessible = true
                            val displayValue = field.get(enumValue) as String
                            Text(text = displayValue)
                        }
                    )
                }
            }
        }
    }
    if (divider.bottom) HorizontalDivider()
}

