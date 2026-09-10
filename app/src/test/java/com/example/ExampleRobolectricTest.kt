package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.PersianUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("تسبیحات حضرت زهرا", appName)
  }

  @Test
  fun `persian digits conversion`() {
    assertEquals("۱ از ۳۴", "${PersianUtils.toPersianDigits(1)} از ${PersianUtils.toPersianDigits(34)}")
    assertEquals("۳۳", PersianUtils.toPersianDigits(33))
  }
}
