package com.example.model

enum class DhikrSection(
    val totalCount: Int,
    val dhikrText: String,
    val soundResName: String
) {
    ALLAHU_AKBAR(34, "الله اکبر", "audio1"),
    ALHAMDULILLAH(33, "الحمدلله", "audio2"),
    SUBHANALLAH(33, "سبحان الله", "audio3")
}

sealed class DhikrScreenState {
    data object Splash : DhikrScreenState()
    
    data class Active(
        val section: DhikrSection,
        val currentIndex: Int // 1-based index: 1..totalCount
    ) : DhikrScreenState()
    
    data object Completed : DhikrScreenState()
}
