package com.reedsloan.beekeepingapp.data.local

data class CheckboxSelectionValues(
    val maxSelectionCount: Int = 1,
    val minSelectionCount: Int = 1,
    val disabledValues: List<String> = listOf(),
    val showSelectionInstructions: Boolean = false,
    val allowCustomValues: Boolean = false,
) {
    // builder
    class Builder {
        private var maxSelectionCount: Int = 1
        private var minSelectionCount: Int = 1
        private var disabledValues: List<String> = listOf()
        private var showSelectionInstructions: Boolean = false
        private var allowCustomValues: Boolean = false

        fun setMaxSelectionCount(maxSelectionCount: Int) =
            apply { this.maxSelectionCount = maxSelectionCount }

        fun setMinSelectionCount(minSelectionCount: Int) =
            apply { this.minSelectionCount = minSelectionCount }

        fun setDisabledValues(disabledValues: List<String>) =
            apply { this.disabledValues = disabledValues }

        fun setShowSelectionInstructions(showSelectionInstructions: Boolean) =
            apply { this.showSelectionInstructions = showSelectionInstructions }

        fun setAllowCustomValues(allowCustomValues: Boolean) =
            apply { this.allowCustomValues = allowCustomValues }

        fun build() = CheckboxSelectionValues(
            maxSelectionCount = maxSelectionCount,
            minSelectionCount = minSelectionCount,
            disabledValues = disabledValues,
            showSelectionInstructions = showSelectionInstructions,
            allowCustomValues = allowCustomValues,
        )
    }
}
